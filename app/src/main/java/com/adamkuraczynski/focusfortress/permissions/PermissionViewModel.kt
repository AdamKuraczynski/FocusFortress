package com.adamkuraczynski.focusfortress.permissions

import android.annotation.SuppressLint
import android.app.Application
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing and checking the status of special
 * permissions required by the application.
 *
 * This class extends [AndroidViewModel] to have access to the application context,
 * which is necessary for checking and requesting permissions.
 *
 * The ViewModel exposes [StateFlow] properties to observe the current status of permissions
 * and provides methods to request permissions when needed.
 *
 * @constructor Creates a [PermissionViewModel] with the given application context.
 *
 * @param application The application context used to access system services.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.5
 *
 */
class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val localHasUsageAccessPermission = MutableStateFlow(checkUsageAccessPermission())
    val hasUsageAccessPermission: StateFlow<Boolean> = localHasUsageAccessPermission

    private val localHasOverlayPermission = MutableStateFlow(checkOverlayPermission())
    val hasOverlayPermission: StateFlow<Boolean> = localHasOverlayPermission

    private val localHasAccessibilityPermission = MutableStateFlow(checkAccessibilityServicePermission())
    val hasAccessibilityPermission: StateFlow<Boolean> = localHasAccessibilityPermission

    private val localHasNotificationPermission = MutableStateFlow(checkNotificationPermission())
    val hasNotificationPermission: StateFlow<Boolean> = localHasNotificationPermission

    /**
     * Updates the status of all permissions by rechecking their current state.
     *
     * This method should be called whenever the app resumes to ensure the permission status is up-to-date.
     */
    fun updatePermissionsStatus() {
        viewModelScope.launch {
            localHasUsageAccessPermission.value = checkUsageAccessPermission()
            localHasOverlayPermission.value = checkOverlayPermission()
            localHasAccessibilityPermission.value = checkAccessibilityServicePermission()
            localHasNotificationPermission.value = checkNotificationPermission()
        }
    }

    /**
     * Checks if the app has Usage Access permission.
     *
     * @return `true` if the permission is granted; `false` otherwise.
     */
    private fun checkUsageAccessPermission(): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * Checks if the app has Overlay permission.
     *
     * @return `true` if the permission is granted; `false` otherwise.
     */
    private fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * Checks if the app has Accessibility Service permission.
     *
     * @return `true` if the permission is granted; `false` otherwise.
     */
    private fun checkAccessibilityServicePermission(): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices != null && enabledServices.contains(context.packageName)
    }

    /**
     * Checks if the app has Notification permission.
     *
     * @return `true` if the permission is granted; `false` otherwise.
     */
    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.areNotificationsEnabled()
        } else {
            // enabled by default on lower sdk
            true
        }
    }

    /**
     * Requests the Usage Access permission by launching the appropriate settings screen.
     */
    fun requestUsageAccessPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Requests the Overlay permission by launching the appropriate settings screen.
     */
    fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:${context.packageName}")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Requests the Accessibility Service permission by launching the appropriate settings screen.
     */
    fun requestPermissionAccess() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Requests the Notification permission by launching the app's notification settings screen.
     */
    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            // enabled by default on lower sdk
        }
    }
}