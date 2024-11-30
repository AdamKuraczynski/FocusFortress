package com.adamkuraczynski.focusfortress.blocking

import android.app.Application
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.BlockedApp
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the list of installed apps and blocked apps.
 *
 * Fetches the list of installed apps and provides functionality to block or unblock apps.
 *
 * @param application The [Application] context used to access system services.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.4
 *
 * @see androidx.lifecycle.AndroidViewModel
 * @see AppInfo
 */
class BlockAppViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager = application.packageManager
    private val blockedAppDao = FocusFortressApp.database.blockedAppDao()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> get() = _installedApps

    val blockedApps: StateFlow<List<BlockedApp>> = blockedAppDao.getBlockedApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchInstalledApps()
    }

    /**
     * Fetches the list of installed apps that can be launched.
     */
    private fun fetchInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            val apps = packages.filter {
                packageManager.getLaunchIntentForPackage(it.packageName) != null
            }.map { appInfo ->
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val appIcon = packageManager.getApplicationIcon(appInfo.packageName)
                AppInfo(
                    packageName = appInfo.packageName,
                    appName = appName,
                    appIcon = appIcon
                )
            }.sortedBy { it.appName } // a -> z
            _installedApps.value = apps
        }
    }

    /**
     * Blocks an app by adding it to the blocked apps database.
     *
     * @param packageName The package name of the app to block.
     * @param appName The display name of the app.
     */
    fun blockApp(packageName: String, appName: String) {
        viewModelScope.launch {
            val blockedApp = BlockedApp(packageName, appName)
            blockedAppDao.insertBlockedApp(blockedApp)
        }
    }

    /**
     * Unblocks an app by removing it from the blocked apps database.
     *
     * @param blockedApp The [BlockedApp] to unblock.
     */
    fun unblockApp(blockedApp: BlockedApp) {
        viewModelScope.launch {
            blockedAppDao.deleteBlockedApp(blockedApp)
        }
    }

}

/**
 * Data class representing information about an installed app.
 *
 * @property packageName The package name of the app.
 * @property appName The display name of the app.
 * @property appIcon The icon drawable of the app.
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable
)