package com.adamkuraczynski.focusfortress.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.adamkuraczynski.focusfortress.R


class NotificationHelper(private val context: Context) {

    companion object {
        private const val FOREGROUND_CHANNEL_ID = "focus_fortress_foreground_channel"
        private const val ACHIEVEMENT_CHANNEL_ID = "focus_fortress_achievement_channel"
        private const val MOTIVATION_CHANNEL_ID = "focus_fortress_motivation_channel"

        private const val FOREGROUND_CHANNEL_NAME = "Foreground"
        private const val ACHIEVEMENT_CHANNEL_NAME = "Achievements"
        private const val MOTIVATION_CHANNEL_NAME = "Motivational"

        private const val FOREGROUND_NOTIFICATION_ID = 1001
        private const val ACHIEVEMENT_NOTIFICATION_ID = 1002
        private const val MOTIVATION_NOTIFICATION_ID = 1003
    }

    init {
        createForegroundChannel()
        createAchievementChannel()
        createMotivationChannel()
    }

    
    private fun createForegroundChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                FOREGROUND_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    
    fun buildForegroundNotification(): Notification {
        return NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
            .setContentTitle("FocusFortress")
            .setContentText("Guarding Your Productivity")
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setOngoing(true)
            .build()
    }

    
    fun getNotificationId(): Int = FOREGROUND_NOTIFICATION_ID


    private fun createAchievementChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ACHIEVEMENT_CHANNEL_ID,
                ACHIEVEMENT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Achievements notifications"
                enableLights(true)
                lightColor = Color.GREEN
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showAchievementNotification(achievementTitle: String) {
        val notification = NotificationCompat.Builder(context, ACHIEVEMENT_CHANNEL_ID)
            .setContentTitle("Achievement Acquired")
            .setContentText(achievementTitle)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(ACHIEVEMENT_NOTIFICATION_ID, notification)
    }

    private fun createMotivationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MOTIVATION_CHANNEL_ID,
                MOTIVATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Motivational messages"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun motivationNotification(message: String) {
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(message)
            .setBigContentTitle("Daily Inspiration")

        val notification = NotificationCompat.Builder(context, MOTIVATION_CHANNEL_ID)
            .setContentTitle("Daily Inspiration")
            .setContentText(message)
            .setStyle(bigTextStyle)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(MOTIVATION_NOTIFICATION_ID, notification)
    }
}