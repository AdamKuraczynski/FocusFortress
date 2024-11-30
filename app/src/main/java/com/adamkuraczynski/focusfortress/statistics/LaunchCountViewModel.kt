package com.adamkuraczynski.focusfortress.statistics

import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing app launch counts over different time periods.
 *
 * This class fetches app usage data from the system using `UsageStatsManager`,
 * filters it to include only user-installed apps, and calculates the number of launches for each app.
 * The processed data is exposed as a `StateFlow` for observation in the UI.
 *
 * @param application The [Application] context used to access system services and resources.
 *
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.4
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var loadJob: Job? = null

    init {
        loadAppLaunchCounts("Day")
    }

    /**
     * Loads app launch counts for the specified period and applies sorting and filtering options.
     *
     * @param period The time period for which to load data ("Day", "3 Days", "Week").
     * @param sortOptionLaunchCount The sorting option for the launch counts.
     * @param minLaunchCount The minimum number of launches to include in the results.
     */
    fun loadAppLaunchCounts(
        period: String,
        sortOptionLaunchCount: SortOptionLaunchCount = SortOptionLaunchCount.LaunchCountDescending,
        minLaunchCount: Int = 0
    ) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            delay(300)
            _isLoading.value = true
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
            }.sortedBy { it.launchCount }

            val filteredApps = appLaunchCountList.filter { it.launchCount >= minLaunchCount }

            val sortedApps = when (sortOptionLaunchCount) {
                SortOptionLaunchCount.LaunchCountDescending -> filteredApps.sortedByDescending { it.launchCount }
                SortOptionLaunchCount.LaunchCountAscending -> filteredApps.sortedBy { it.launchCount }
                SortOptionLaunchCount.AppNameAscending -> filteredApps.sortedBy { it.appName }
                SortOptionLaunchCount.AppNameDescending -> filteredApps.sortedByDescending { it.appName }
            }

            _appLaunchCounts.value = sortedApps
            _isLoading.value = false
        }
    }

    /**
     * Determines if the given [ApplicationInfo] represents a user-installed app.
     *
     * @param appInfo The application info to check.
     * @return `true` if it's a user app; `false` otherwise.
     */
    private fun isUserApp(appInfo: ApplicationInfo): Boolean {
        return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 ||
                (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
    }
}

/**
 * Data class representing the launch count of an app.
 *
 * @property appName The display name of the app.
 * @property packageName The package name of the app.
 * @property launchCount The number of times the app was launched.
 * @property appIcon The icon drawable of the app.
 */
data class AppLaunchCount(
    val appName: String,
    val packageName: String,
    val launchCount: Int,
    val appIcon: android.graphics.drawable.Drawable? = null
)

/**
 * Enum class representing the sorting options for app launch counts.
 */
enum class SortOptionLaunchCount {
    LaunchCountDescending,
    LaunchCountAscending,
    AppNameAscending,
    AppNameDescending
}