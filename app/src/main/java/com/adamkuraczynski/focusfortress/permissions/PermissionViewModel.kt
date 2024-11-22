
package com.adamkuraczynski.focusfortress.permissions

import android.annotation.SuppressLint
import android.app.Application
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
 * @author Adam Kuraczyński
 * @version 1.8
 *
 * @constructor Creates a [PermissionViewModel] with the given application context.
 *
 * @param application The application context used to access system services.
 */

class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val localHasUsageAccessPermission = MutableStateFlow(checkUsageAccessPermission())
    val hasUsageAccessPermission: StateFlow<Boolean> = localHasUsageAccessPermission

    private val localHasOverlayPermission = MutableStateFlow(checkOverlayPermission())
    val hasOverlayPermission: StateFlow<Boolean> = localHasOverlayPermission

    private val localHasNotificationPermission = MutableStateFlow(checkNotificationListenerPermission())
    val hasNotificationPermission: StateFlow<Boolean> = localHasNotificationPermission

    private val localHasAccessibilityPermission = MutableStateFlow(checkAccessibilityServicePermission())
    val hasAccessibilityPermission: StateFlow<Boolean> = localHasAccessibilityPermission

    fun updatePermissionsStatus() {
        viewModelScope.launch {
            localHasUsageAccessPermission.value = checkUsageAccessPermission()
            localHasOverlayPermission.value = checkOverlayPermission()
            localHasNotificationPermission.value = checkNotificationListenerPermission()
            localHasAccessibilityPermission.value = checkAccessibilityServicePermission()
        }
    }

    private fun checkUsageAccessPermission(): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    private fun checkNotificationListenerPermission(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners != null && enabledListeners.contains(context.packageName)
    }

    private fun checkAccessibilityServicePermission(): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices != null && enabledServices.contains(context.packageName)
    }

    fun requestUsageAccessPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:${context.packageName}")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun requestNotificationAccess() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun requestPermissionAccess() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
