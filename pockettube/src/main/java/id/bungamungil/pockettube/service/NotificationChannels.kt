package id.bungamungil.pockettube.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object NotificationChannels {

    const val downloadServiceNotificationChannelId = "download-service-foreground";

    private const val downloadServiceNotificationChannelName = "Download Progress";

    @RequiresApi(Build.VERSION_CODES.O)
    private val downloadServiceNotification = NotificationChannel(
        downloadServiceNotificationChannelId,
        downloadServiceNotificationChannelName,
        NotificationManager.IMPORTANCE_HIGH
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerAll(context: Context) {
        ContextCompat.getSystemService(context, NotificationManager::class.java)
            ?.createNotificationChannel(downloadServiceNotification)
    }

}