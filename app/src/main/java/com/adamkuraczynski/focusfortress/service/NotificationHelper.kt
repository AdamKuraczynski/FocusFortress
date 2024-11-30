package com.adamkuraczynski.focusfortress.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.adamkuraczynski.focusfortress.R

/**
 * Helper class for creating and managing notifications for the FocusFortress service.
 *
 * This class handles the creation of notification channels and building
 * the foreground notification required for the service.
 *
 * @param context The [Context] used to access system services.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.1
 *
 * @see android.app.Notification
 * @see android.app.NotificationChannel
 * @see androidx.core.app.NotificationCompat
 */
class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "focus_fortress_channel"
        private const val CHANNEL_NAME = "FocusFortress Service Channel"
        private const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    /**
     * Creates the notification channel required for API level 26 and above.
     */
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

    /**
     * Builds the foreground notification for the service.
     *
     * @return The [Notification] to be displayed.
     */
    fun buildForegroundNotification(): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("FocusFortress")
            .setContentText("Guarding Your Productivity")
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setOngoing(true)
            .build()
    }

    /**
     * Retrieves the notification ID used for the service notification.
     *
     * @return The notification ID as an [Int].
     */
    fun getNotificationId(): Int = NOTIFICATION_ID
}
