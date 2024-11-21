package com.adamkuraczynski.focusfortress.service

import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AppLaunchCount(
    val appName: String,
    val packageName: String,
    val launchCount: Int,
    val appIcon: android.graphics.drawable.Drawable? = null
)

/**
 * ViewModel responsible for managing app launch counts over different time periods.
 *
 * This class fetches app usage data from the system using `UsageStatsManager`,
 * filters it to include only user-installed apps, and calculates the number of launches for each app.
 * The processed data is exposed as a `StateFlow` for observation in the UI.
 *
 * @author Adam Kuraczyński
 * @version 1.6
 *
 * @param application The [Application] context used to access system services and resources.
 *
 */

class LaunchCountViewModel(application: Application) : AndroidViewModel(application) {

    // monitor without changing
    private val _appLaunchCounts = MutableStateFlow<List<AppLaunchCount>>(emptyList())
    // exposed to the UI - immutable outside the ViewModel
    val appLaunchCounts: StateFlow<List<AppLaunchCount>> = _appLaunchCounts

    private val usageStatsManager =
        application.getSystemService(Application.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = application.packageManager

    init {
        loadAppLaunchCounts("Day")
    }

    fun loadAppLaunchCounts(period: String) {
        viewModelScope.launch {
            val endTime = System.currentTimeMillis()
            val startTime = when (period) {
                "Day" -> endTime - (1 * 24 * 60 * 60 * 1000)
                "3 Days" -> endTime - (3 * 24 * 60 * 60 * 1000)
                "Week" -> endTime - (7 * 24 * 60 * 60 * 1000)
                else -> endTime - (1 * 24 * 60 * 60 * 1000)
            }

            val launchCounts = mutableMapOf<String, Int>()

            val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
            val event = UsageEvents.Event()

            // counting
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    val packageName = event.packageName
                    launchCounts[packageName] = launchCounts.getOrDefault(packageName, 0) + 1
                }
            }

            // only installed - not system
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { isUserApp(it) }

            // each found app converted to AppLaunchCount class
            val appLaunchCountList = launchCounts.mapNotNull { (packageName, count) ->
                try {
                    val appInfo = installedApps.find { it.packageName == packageName }
                    if (appInfo != null) {
                        val appName = packageManager.getApplicationLabel(appInfo).toString()
                        val appIcon = packageManager.getApplicationIcon(appInfo)
                        AppLaunchCount(
                            appName = appName,
                            packageName = packageName,
                            launchCount = count,
                            appIcon = appIcon
                        )
                    } else {
                        null
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
            }.sortedByDescending { it.launchCount }

            _appLaunchCounts.value = appLaunchCountList
        }
    }

    private fun isUserApp(appInfo: ApplicationInfo): Boolean {
        return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
    }
}
