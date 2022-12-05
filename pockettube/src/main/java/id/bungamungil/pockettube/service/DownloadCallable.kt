package id.bungamungil.pockettube.service

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.yausername.youtubedl_android.DownloadProgressCallback
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.YoutubeDLResponse
import id.bungamungil.pockettube.R
import java.io.File
import java.net.URL
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
        val filenameFromIntent = intent.getStringExtra(DOWNLOAD_FILE_NAME)
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
        if (filenameFromIntent == null) {
            throw RuntimeException("Download file name should not be null")
        }
        if (fileExtension == null) {
            throw RuntimeException("Download file extension should not be null")
        }
        val downloadDir = getDownloadLocation()
        val request = YoutubeDLRequest(url)
        val filename = filenameFromIntent.replace(Regex("\\W+"), "_")
        val filenameWithExtension = "${filename}.${fileExtension}"
        val path = "${downloadDir.absolutePath}/$filenameWithExtension"
        request.addOption("--no-mtime")
        request.addOption("--downloader", "libaria2c.so")
        request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
        request.addOption("-f", format)
        request.addOption("-o", path)
        val response = YoutubeDL.getInstance().execute(request, name, callback)
        saveFileToExternalStorage(path, filename, fileExtension)
        return response
    }

    private fun saveFileToExternalStorage(source: String, filename: String, extension: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.app_name))
            if (!dir.exists()) dir.mkdir()
            val sourceFile = File(source)
            sourceFile.copyTo(File(dir, "${filename}.${extension}"))
            sourceFile.delete()
        } else {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "${filename}.${extension}")
                put(MediaStore.MediaColumns.MIME_TYPE, "video/${extension}")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/${context.getString(R.string.app_name)}")
            }
            val resolver = context.contentResolver
            val target = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (target != null) {
                URL("file://$source").openStream().use { input ->
                    resolver.openOutputStream(target).use { output ->
                        input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                    }
                }
            }
            val sourceFile = File(source)
            sourceFile.delete()
        }
    }

    private fun getDownloadLocation(): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.app_name))
        if (!dir.exists()) dir.mkdir()
        return dir
    }

}