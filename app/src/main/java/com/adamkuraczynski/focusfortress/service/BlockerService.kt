package com.adamkuraczynski.focusfortress.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent
import android.net.Uri.parse
import android.util.Log
import com.adamkuraczynski.focusfortress.blocking.BlockedActivity
import com.adamkuraczynski.focusfortress.database.FocusFortressApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

/**
 * Accessibility Service that blocks apps, websites, and keywords based on user-defined settings.
 *
 * This service monitors accessibility events to detect when a blocked app is opened,
 * a blocked website is accessed, or a blocked keyword is searched. When such an event
 * is detected, it launches the [BlockedActivity] to inform the user.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.6
 *
 * @see android.accessibilityservice.AccessibilityService
 * @see com.adamkuraczynski.focusfortress.blocking.BlockedActivity
 */
class BlockerService : AccessibilityService() {

    private val loggingTag = "BlockerService"
    private val enableLogging = true

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var notificationHelper: NotificationHelper

    /**
     * Private logging function for debug messages.
     */
    private fun logDebug(message: String) {
        if (enableLogging) {
            Log.d(loggingTag, message)
        }
    }

    /**
     * Private logging function for error messages.
     */
    private fun logError(message: String, throwable: Throwable? = null) {
        if (enableLogging) {
            Log.e(loggingTag, message, throwable)
        }
    }

    /**
     * Called when the service is created.
     *
     * Initializes the notification helper and starts the service in the foreground.
     */
    override fun onCreate() {
        super.onCreate()
        logDebug("onCreate called")
        notificationHelper = NotificationHelper(this)
        val notification = notificationHelper.buildForegroundNotification()
        startForeground(notificationHelper.getNotificationId(), notification)
    }

    /**
     * Data class representing supported browsers and their address bar IDs.
     *
     * @property packageName The package name of the browser.
     * @property addressBarIds The resource ID of the browser's address bar.
     */
    data class SupportedBrowserConfig(
        val packageName: String,
        val addressBarIds: List<String>
    )

    private val supportedBrowsers = listOf(
        SupportedBrowserConfig(
            packageName = "com.android.chrome",
            addressBarIds = listOf(
                "com.android.chrome:id/url_bar",
                "com.android.chrome:id/url_bar_title"
            )
        ),
        SupportedBrowserConfig(
            packageName= "org.mozilla.firefox",
            addressBarIds = listOf(
                "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"
            )
        ),
        SupportedBrowserConfig(
            packageName= "com.opera.browser",
            addressBarIds = listOf(
                "com.opera.browser:id/url_field"
            )
        ),
        SupportedBrowserConfig(
            packageName= "com.opera.mini.native",
            addressBarIds = listOf(
                "com.opera.mini.native:id/url_field"
            )
        )
    )
    private var lastBlockedPackageName: String? = null
    private var lastBlockedDomain: String? = null
    private var lastBlockedKeyword: String? = null
    private var lastBlockedTimestamp: Long = 0

    /**
     * Called when the system wants to interrupt the accessibility feedback.
     */
    override fun onInterrupt() {
        logDebug("onInterrupt called")
    }

    /**
     * Called when the service is destroyed.
     *
     * Cancels the coroutine scope to clean up resources.
     */
    override fun onDestroy() {
        logDebug("onDestroy called")
        serviceScope.cancel()
        super.onDestroy()
    }

    /**
     * Checks if blocking is currently active based on the user's schedule.
     *
     * @return `true` if blocking is active; `false` otherwise.
     */
    private suspend fun isBlockingActive(): Boolean {
        logDebug("Checking if blocking is active")
        val activeSchedule = FocusFortressApp.database.scheduleDao().getActiveSchedule().firstOrNull()
        if (activeSchedule == null) {
            logDebug("No active schedule found")
            return false
        }

        val calendar = Calendar.getInstance()
        val currentDayNumber = calendar.get(Calendar.DAY_OF_WEEK) // 1 Sunday to 7 Saturday
        val currentTime = String.format(Locale.US, "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

        val scheduledDayNumbers = activeSchedule.daysOfWeek.split(",").mapNotNull { it.toIntOrNull() }
        val startTime = activeSchedule.startTime
        val endTime = activeSchedule.endTime

        val isDayMatching = currentDayNumber in scheduledDayNumbers

        val isTimeMatching = if (startTime <= endTime) {
            // time - midnight not crossing
            currentTime in startTime..endTime
        } else {
            // time - midnight crossing
            currentTime >= startTime || currentTime <= endTime
        }

        logDebug("Current day number: $currentDayNumber, Current time: $currentTime")
        logDebug("Scheduled day numbers: $scheduledDayNumbers, Start time: $startTime, End time: $endTime")
        logDebug("isDayMatching: $isDayMatching, isTimeMatching: $isTimeMatching")

        val isActive = isDayMatching && isTimeMatching
        logDebug("Blocking is ${if (isActive) "active" else "inactive"}")
        return isActive
    }

    /**
     * Called when an accessibility event is received.
     *
     * @param event The [AccessibilityEvent] received.
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) {
            logDebug("Received null event")
            return
        }

        val eventType = event.eventType
        val packageName = event.packageName?.toString()
        if (packageName == null) {
            logDebug("Event package name is null")
            return
        }

        logDebug("Received event: Type=$eventType, PackageName=$packageName")

        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {

                val browserConfig = supportedBrowsers.find { it.packageName == packageName }
                if (browserConfig != null) {
                    logDebug("Event from supported browser: $packageName")
                    handleWebsiteBlocking(event, browserConfig)
                    handleKeywordBlocking(event, browserConfig)
                } else {
                    logDebug("Event from app: $packageName")
                    handleAppBlocking(event)
                }
            }
            else -> {
                logDebug("Unhandled event type: $eventType")
            }
        }
    }

    /**
     * Handles blocking of applications.
     *
     * @param event The [AccessibilityEvent] associated with the app launch.
     */
    private fun handleAppBlocking(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return

        logDebug("handleAppBlocking called for package: $packageName")

        if (packageName == this.packageName) {
            logDebug("Ignoring own package")
            return
        }

        if (packageName == lastBlockedPackageName && System.currentTimeMillis() - lastBlockedTimestamp < 3000) {
            logDebug("Recently blocked package, skipping: $packageName")
            return
        }

        serviceScope.launch {
            if (!isBlockingActive()) {
                logDebug("Blocking is not active, returning")
                return@launch
            }

            logDebug("Checking if app is blocked: $packageName")
            val isBlocked = FocusFortressApp.database.blockedAppDao().isAppBlocked(packageName)
            logDebug("Is app blocked: $isBlocked")

            if (isBlocked) {
                withContext(Dispatchers.Main) {
                    lastBlockedPackageName = packageName
                    lastBlockedTimestamp = System.currentTimeMillis()

                    logDebug("Blocking app: $packageName")

                    val intent = Intent(this@BlockerService, BlockedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("BLOCK_TYPE", "app")
                    startActivity(intent)
                }
            }
        }
    }

    /**
     * Handles blocking of websites.
     *
     * @param event The [AccessibilityEvent] associated with the browser activity.
     * @param browserConfig The [SupportedBrowserConfig] of the browser.
     */
    private fun handleWebsiteBlocking(event: AccessibilityEvent, browserConfig: SupportedBrowserConfig) {
        val source = event.source
        if (source == null) {
            logDebug("Event source is null")
            return
        }

        val url = captureUrl(source, browserConfig)
        logDebug("Captured URL: $url")

        if (url != null) {
            val domain = extractDomain(url)?.lowercase(Locale.getDefault())?.removePrefix("www.")
            logDebug("Extracted domain: $domain")

            if (domain != null) {

                if (domain == lastBlockedDomain && System.currentTimeMillis() - lastBlockedTimestamp < 500) {
                    logDebug("Recently blocked domain, skipping: $domain")
                    return
                }

                serviceScope.launch {
                    if (!isBlockingActive()) {
                        logDebug("Blocking is not active, returning")
                        return@launch
                    }

                    logDebug("Checking if website is blocked: $domain")
                    val isBlocked = FocusFortressApp.database.blockedWebsiteDao().isWebsiteBlocked(domain)
                    logDebug("Is website blocked: $isBlocked")

                    if (isBlocked) {
                        withContext(Dispatchers.Main) {
                            lastBlockedDomain = domain
                            lastBlockedTimestamp = System.currentTimeMillis()

                            logDebug("Blocking website: $domain")

                            val intent = Intent(this@BlockerService, BlockedActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                putExtra("BLOCK_TYPE", "website")
                                putExtra("BROWSER_PACKAGE", browserConfig.packageName)
                            }
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    /**
     * Extracts the domain from a given URL.
     *
     * @param url The URL string.
     * @return The domain if extraction is successful; `null` otherwise.
     */
    private fun extractDomain(url: String): String? {
        return try {
            var fixedUrl = url
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                fixedUrl = "https://$url"
            }
            val uri = parse(fixedUrl)
            val domain = uri.host?.lowercase(Locale.getDefault())?.removePrefix("www.")
            domain
        } catch (e: Exception) {
            logError("Error extracting domain from URL: $url", e)
            null
        }
    }

    /**
     * Handles blocking of keywords in search queries.
     *
     * @param event The [AccessibilityEvent] associated with the browser activity.
     * @param browserConfig The [SupportedBrowserConfig] of the browser.
     */
    private fun handleKeywordBlocking(event: AccessibilityEvent, browserConfig: SupportedBrowserConfig) {
        val packageName = event.packageName?.toString() ?: return

        logDebug("handleKeywordBlocking called for package: $packageName")

        if (packageName == this.packageName) {
            logDebug("Ignoring own package")
            return
        }

        val source = event.source
        if (source == null) {
            logDebug("Event source is null")
            return
        }

        val url = captureUrl(source, browserConfig)
        logDebug("Captured URL for keyword blocking: $url")

        if (url != null) {
            val searchText = extractSearchTextFromUrl(url)
            logDebug("Extracted search text from URL: $searchText")
            if (searchText != null) {
                handleKeywordBlockingHelper(searchText, browserConfig)
            }
        } else {
            val eventText = event.text.joinToString(" ")
            logDebug("Event text for keyword blocking: $eventText")
            if (eventText.isNotEmpty()) {
                val normalizedText = eventText.lowercase(Locale.getDefault())
                handleKeywordBlockingHelper(normalizedText, browserConfig)
            }
        }
    }

    /**
     * Extracts the search text from a URL.
     *
     * @param url The URL string.
     * @return The search text if present; `null` otherwise.
     */
    private fun extractSearchTextFromUrl(url: String): String? {
        val uri = try {
            parse(url)
        } catch (e: Exception) {
            logError("Error parsing URL for search text: $url", e)
            return null
        }

        val queryParameters = listOf("q", "query", "search")
        val query = queryParameters.firstNotNullOfOrNull { uri.getQueryParameter(it) }

        return if (!query.isNullOrEmpty()) {
            query
        } else if (uri.host == null && url.isNotEmpty()) {
            url
        } else {
            null
        }
    }

    /**
     * Helper function to handle keyword blocking logic.
     *
     * @param searchText The text to check for blocked keywords.
     * @param browserConfig The [SupportedBrowserConfig] of the browser.
     */
    private fun handleKeywordBlockingHelper(searchText: String, browserConfig: SupportedBrowserConfig) {
        if (searchText.isEmpty()) {
            logDebug("Search text is empty, returning")
            return
        }

        logDebug("handleKeywordBlockingHelper called with searchText: $searchText")

        serviceScope.launch {
            if (!isBlockingActive()) {
                logDebug("Blocking is not active, returning")
                return@launch
            }

            val blockedKeywords = FocusFortressApp.database.blockedKeywordDao().getBlockedKeywords().firstOrNull()
            if (blockedKeywords.isNullOrEmpty()) {
                logDebug("No blocked keywords found")
                return@launch
            }

            val normalizedQuery = searchText.lowercase(Locale.getDefault())
            logDebug("Normalized search text: $normalizedQuery")

            val matchedKeyword = blockedKeywords.find { keyword ->
                normalizedQuery.contains(keyword.keyword, ignoreCase = true)
            }

            if (matchedKeyword != null) {
                withContext(Dispatchers.Main) {
                    if (matchedKeyword.keyword == lastBlockedKeyword &&
                        System.currentTimeMillis() - lastBlockedTimestamp < 3000
                    ) {
                        logDebug("Recently blocked keyword, skipping: ${matchedKeyword.keyword}")
                        return@withContext
                    }

                    lastBlockedKeyword = matchedKeyword.keyword
                    lastBlockedTimestamp = System.currentTimeMillis()

                    logDebug("Blocking keyword: ${matchedKeyword.keyword}")

                    val intent = Intent(this@BlockerService, BlockedActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra("BLOCK_TYPE", "keyword")
                        putExtra("BROWSER_PACKAGE", browserConfig.packageName)
                        putExtra("BLOCKED_KEYWORD", matchedKeyword.keyword)
                    }
                    startActivity(intent)
                }
            } else {
                logDebug("No matching blocked keyword found in search text")
            }
        }
    }

    /**
     * Captures the URL from the browser's address bar using accessibility node info.
     *
     * @param info The [AccessibilityNodeInfo] of the current node.
     * @param browserConfig The [SupportedBrowserConfig] of the browser.
     * @return The URL string if found; `null` otherwise.
     */
    private fun captureUrl(info: AccessibilityNodeInfo, browserConfig: SupportedBrowserConfig): String? {
        for (addressBarId in browserConfig.addressBarIds) {
            try {
                val nodes = info.findAccessibilityNodeInfosByViewId(addressBarId)
                if (!nodes.isNullOrEmpty()) {
                    val url = nodes[0].text?.toString()
                    logDebug("Captured URL from address bar ID $addressBarId: $url")
                    return url
                } else {
                    logDebug("No nodes found for address bar ID: $addressBarId")
                }
            } catch (e: Exception) {
                logError("Error capturing URL from address bar ID: $addressBarId", e)
            }
        }
        return null
    }
}