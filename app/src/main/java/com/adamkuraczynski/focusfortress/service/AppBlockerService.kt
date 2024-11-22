package com.adamkuraczynski.focusfortress.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.adamkuraczynski.focusfortress.blocking.BlockedAppActivity
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import kotlinx.coroutines.runBlocking


class AppBlockerService : AccessibilityService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("AppBlockerService", "Service Created")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("AppBlockerService", "Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d("AppBlockerService", "Accessibility Event Received: ${event?.eventType}")

        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            Log.d("AppBlockerService", "App Launched: $packageName")

            // coroutine to check if the app is blocked
            runBlocking {
                // database query
                val isBlocked = FocusFortressApp.database.blockedAppDao().isAppBlocked(packageName)
                if (isBlocked) {
                    Log.d("AppBlockerService", "Blocking app: $packageName")
                    // foreground popup
                    val intent = Intent(this@AppBlockerService, BlockedAppActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d("AppBlockerService", "Service Interrupted")
    }
}
