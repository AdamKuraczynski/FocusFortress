package com.adamkuraczynski.focusfortress.blocking

import android.app.Application
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adamkuraczynski.focusfortress.database.BlockedApp
import com.adamkuraczynski.focusfortress.FocusFortressApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


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

    private val _uiEvents = MutableSharedFlow<ToastEvent>()
    val uiEvents = _uiEvents.asSharedFlow()
    
    fun blockApp(packageName: String, appName: String) {
        viewModelScope.launch {
            val blockedApp = BlockedApp(packageName, appName)
            blockedAppDao.insertBlockedApp(blockedApp)
            val count = blockedAppDao.getBlockedAppsCount()
            _uiEvents.emit(ToastEvent.ShowToast("app", count))
        }
    }

    
    fun unblockApp(blockedApp: BlockedApp) {
        viewModelScope.launch {
            blockedAppDao.deleteBlockedApp(blockedApp)
        }
    }

}


data class AppInfo(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable
)