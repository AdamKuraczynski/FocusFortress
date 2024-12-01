package com.adamkuraczynski.focusfortress.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.adamkuraczynski.focusfortress.R


class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "focus_fortress_channel"
        private const val CHANNEL_NAME = "FocusFortress Service Channel"
        private const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    
    fun buildForegroundNotification(): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("FocusFortress")
            .setContentText("Guarding Your Productivity")
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setOngoing(true)
            .build()
    }

    
    fun getNotificationId(): Int = NOTIFICATION_ID
}
