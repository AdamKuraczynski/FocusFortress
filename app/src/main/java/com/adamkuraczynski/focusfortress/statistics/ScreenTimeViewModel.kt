package com.adamkuraczynski.focusfortress.statistics

import android.app.Application
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
 * ViewModel responsible for managing app usage times over different time periods.
 *
 * This class fetches app usage statistics from the system using `UsageStatsManager`,
 * filters it to include only user-installed apps, and calculates the total usage time for each app.
 * The processed data is exposed as a `StateFlow` for observation in the UI.
 *
 * @param application The [Application] context used to access system services and resources.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.4
 *
 */
class ScreenTimeViewModel(application: Application) : AndroidViewModel(application) {

    private val _appUsageTimes = MutableStateFlow<List<AppUsage>>(emptyList())
    val appUsageTimes: StateFlow<List<AppUsage>> = _appUsageTimes

    private val usageStatsManager =
        application.getSystemService(Application.USAGE_STATS_SERVICE) as UsageStatsManager

    private val packageManager = application.packageManager

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var loadJob: Job? = null

    init {
        loadAppUsageTimes("Day")
    }

    /**
     * Loads app usage times for the specified period and applies sorting and filtering options.
     *
     * @param period The time period for which to load data ("Day", "3 Days", "Week").
     * @param sortOption The sorting option for the usage times.
     * @param minUsageTimeMillis The minimum usage time in milliseconds to include in the results.
     */
    fun loadAppUsageTimes(
        period: String,
        sortOption: SortOptionScreenTime = SortOptionScreenTime.UsageTimeDescending,
        minUsageTimeMillis: Long = 0
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

            val usageTimes = mutableMapOf<String, Long>()

            val usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
            )

            for (usageStats in usageStatsList) {
                val packageName = usageStats.packageName
                val totalTime = usageStats.totalTimeInForeground
                if (totalTime > 0) {
                    usageTimes[packageName] = usageTimes.getOrDefault(packageName, 0L) + totalTime
                }
            }

            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { isUserApp(it) }

            val appUsageList = usageTimes.mapNotNull { (packageName, usageTime) ->
                try {
                    val appInfo = installedApps.find { it.packageName == packageName }
                    if (appInfo != null) {
                        val appName = packageManager.getApplicationLabel(appInfo).toString()
                        val appIcon = packageManager.getApplicationIcon(appInfo)
                        AppUsage(
                            appName = appName,
                            packageName = packageName,
                            usageTimeMillis = usageTime,
                            appIcon = appIcon
                        )
                    } else {
                        null
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
            }

            val filteredApps = appUsageList.filter { it.usageTimeMillis >= minUsageTimeMillis }
            val sortedApps = when (sortOption) {
                SortOptionScreenTime.UsageTimeDescending -> filteredApps.sortedByDescending { it.usageTimeMillis }
                SortOptionScreenTime.UsageTimeAscending -> filteredApps.sortedBy { it.usageTimeMillis }
                SortOptionScreenTime.AppNameAscending -> filteredApps.sortedBy { it.appName }
                SortOptionScreenTime.AppNameDescending -> filteredApps.sortedByDescending { it.appName }
            }
            _appUsageTimes.value = sortedApps
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
 * Data class representing the usage statistics of an app.
 *
 * @property appName The display name of the app.
 * @property packageName The package name of the app.
 * @property usageTimeMillis The total usage time in milliseconds.
 * @property appIcon The icon drawable of the app.
 */
data class AppUsage(
    val appName: String,
    val packageName: String,
    val usageTimeMillis: Long,
    val appIcon: android.graphics.drawable.Drawable? = null
)

/**
 * Enum class representing the sorting options for screen time usage.
 */
enum class SortOptionScreenTime {
    UsageTimeDescending,
    UsageTimeAscending,
    AppNameAscending,
    AppNameDescending
}