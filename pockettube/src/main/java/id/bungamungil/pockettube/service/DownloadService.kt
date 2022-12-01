package id.bungamungil.pockettube.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import id.bungamungil.pockettube.R


class DownloadService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /**
         * Todo: Replace code blocks below with actual code downloading.
         * Pass URL from Intent
         */
        Thread {
            while (true) {
                Log.e("Service", "Service is running...")
                try {
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        val notification: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification
                .Builder(this, NotificationChannels.downloadServiceNotificationChannelId)
        } else {
            Notification.Builder(this)
        }
        notification
            .setContentText("Service is running")
            .setContentTitle("Service enabled")
            .setSmallIcon(R.drawable.ic_launcher_background)

        startForeground(1001, notification.build())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}