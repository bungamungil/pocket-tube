package id.bungamungil.pockettube.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.yausername.youtubedl_android.BuildConfig
import com.yausername.youtubedl_android.YoutubeDLResponse
import id.bungamungil.pockettube.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class DownloadService: Service() {

    companion object {

        var counter = 1

    }

    private val compositeDisposable = CompositeDisposable()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val downloadId = intent.getIntExtra(DownloadCallable.DOWNLOAD_ID, 0)
        if (downloadId == 0) {
            return super.onStartCommand(intent, flags, startId)
        }

        val displayName = intent.getStringExtra(DownloadCallable.DOWNLOAD_DISPLAY_NAME)

        val download = DownloadCallable(this, intent) { progress, etaInSeconds, line ->
            Handler(Looper.getMainLooper()).run {
                val notification = createNotificationBuilder()
                    .setContentText("Download is on progress")
                    .setContentTitle("Downloading $displayName, eta ${etaInSeconds}s ...")
                    .setProgress(100, progress.toInt(), false)
                    .setSmallIcon(R.drawable.download)
                startForeground(downloadId, notification.build())
            }
        }
        val disposable = Observable.fromCallable(download)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onNext, this::onError)
        compositeDisposable.add(disposable)

        val notification = createNotificationBuilder()
            .setContentText("Download is starting")
            .setContentTitle("Downloading $displayName ...")
            .setProgress(100, 0, true)
            .setSmallIcon(R.drawable.download)
        startForeground(downloadId, notification.build())

        counter += 1
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createNotificationBuilder(): Notification.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification
                .Builder(this, NotificationChannels.downloadServiceNotificationChannelId)
        } else {
            Notification.Builder(this)
        }
    }

    private fun onNext(youtubeDLResponse: YoutubeDLResponse) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    private fun onError(reason: Throwable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        if (BuildConfig.DEBUG) Log.e("download-service", reason.localizedMessage, reason)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

}