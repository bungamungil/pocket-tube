package id.bungamungil.pockettube.service

import android.content.Context
import android.content.Intent
import android.os.Build
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
        const val DOWNLOAD_FILE_NAME = "DOWNLOAD_FILE_NAME"
        const val DOWNLOAD_FILE_EXTENSION = "DOWNLOAD_FILE_EXTENSION"

    }

    override fun call(): YoutubeDLResponse {
        val url = intent.getStringExtra(DOWNLOAD_URL)
        val format = intent.getStringExtra(DOWNLOAD_FORMAT)
        val name = intent.getStringExtra(DOWNLOAD_NAME)
        val filename = intent.getStringExtra(DOWNLOAD_FILE_NAME)
        val fileExtension = intent.getStringExtra(DOWNLOAD_FILE_EXTENSION)
        if (url == null) {
            throw RuntimeException("Download url should not be null")
        }
        if (format == null) {
            throw RuntimeException("Download format should not be null")
        }
        if (name == null) {
            throw RuntimeException("Download name should not be null")
        }
        if (filename == null) {
            throw RuntimeException("Download file name should not be null")
        }
        if (fileExtension == null) {
            throw RuntimeException("Download file extension should not be null")
        }
        val downloadDir = getDownloadLocation()
        val request = YoutubeDLRequest(url)
        val filenameWithExtension = "${filename.replace(Regex("\\W+"), "_")}.${fileExtension}"
        val path = "${downloadDir.absolutePath}/$filenameWithExtension"
        request.addOption("--no-mtime")
        request.addOption("--downloader", "libaria2c.so")
        request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
        request.addOption("-f", format)
        request.addOption("-o", path)
        val response = YoutubeDL.getInstance().execute(request, name, callback)
        saveFileToExternalStorage(path, filenameWithExtension)
        return response
    }

    private fun saveFileToExternalStorage(source: String, fileName: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.app_name))
            if (!dir.exists()) dir.mkdir()
            val sourceFile = File(source)
            sourceFile.copyTo(File(dir, fileName))
            sourceFile.delete()
        }
    }

    private fun getDownloadLocation(): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.app_name))
        if (!dir.exists()) dir.mkdir()
        return dir
    }

}