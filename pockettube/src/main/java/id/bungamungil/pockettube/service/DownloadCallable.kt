package id.bungamungil.pockettube.service

import android.content.Context
import android.content.Intent
import android.os.Environment
import com.yausername.youtubedl_android.DownloadProgressCallback
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.YoutubeDLResponse
import id.bungamungil.pockettube.R
import java.io.File
import java.util.concurrent.Callable

class DownloadCallable(private val context: Context, private val intent: Intent, private val callback: DownloadProgressCallback) : Callable<YoutubeDLResponse> {

    companion object {

        const val DOWNLOAD_URL = "DOWNLOAD_URL"
        const val DOWNLOAD_FORMAT = "DOWNLOAD_FORMAT"
        const val DOWNLOAD_NAME = "DOWNLOAD_NAME"
        const val DOWNLOAD_ID = "DOWNLOAD_ID"
        const val DOWNLOAD_DISPLAY_NAME = "DOWNLOAD_DISPLAY_NAME"

    }

    override fun call(): YoutubeDLResponse {
        val url = intent.getStringExtra(DOWNLOAD_URL)
        val format = intent.getStringExtra(DOWNLOAD_FORMAT)
        val name = intent.getStringExtra(DOWNLOAD_NAME)
        if (url == null) {
            throw RuntimeException("Download url should not be null")
        }
        if (format == null) {
            throw RuntimeException("Download format should not be null")
        }
        if (name == null) {
            throw RuntimeException("Download name should not be null")
        }
        val downloadDir = getDownloadLocation()
        val request = YoutubeDLRequest(url)
        request.addOption("--no-mtime")
        request.addOption("--downloader", "libaria2c.so")
        request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
        request.addOption("-f", format)
        request.addOption("-o", downloadDir.absolutePath + "/%(title)s.%(ext)s")
        return YoutubeDL.getInstance().execute(request, name, callback)
    }

    private fun getDownloadLocation(): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val youtubeDLDir = File(downloadsDir, context.getString(R.string.app_name))
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir()
        return youtubeDLDir
    }

}