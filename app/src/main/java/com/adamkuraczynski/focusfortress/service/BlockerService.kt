package com.adamkuraczynski.focusfortress.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent
import android.net.Uri.parse
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
 * **Version:** 1.5
 *
 * @see android.accessibilityservice.AccessibilityService
 * @see com.adamkuraczynski.focusfortress.blocking.BlockedActivity
 */
class BlockerService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var notificationHelper: NotificationHelper

    /**
     * Called when the service is created.
     *
     * Initializes the notification helper and starts the service in the foreground.
     */
    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        val notification = notificationHelper.buildForegroundNotification()
        startForeground(notificationHelper.getNotificationId(), notification)
    }

    /**
     * Data class representing supported browsers and their address bar IDs.
     *
     * @property packageName The package name of the browser.
     * @property addressBarId The resource ID of the browser's address bar.
     */
    data class SupportedBrowserConfig(
        val packageName: String,
        val addressBarId: String
    )

    private val supportedBrowsers = listOf(
        SupportedBrowserConfig("com.android.chrome", "com.android.chrome:id/url_bar"),
        SupportedBrowserConfig("org.mozilla.firefox", "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"),
    )
    private var lastBlockedPackageName: String? = null
    private var lastBlockedDomain: String? = null
    private var lastBlockedKeyword: String? = null
    private var lastBlockedTimestamp: Long = 0

    /**
     * Called when the system wants to interrupt the accessibility feedback.
     */
    override fun onInterrupt() {
    }

    /**
     * Called when the service is destroyed.
     *
     * Cancels the coroutine scope to clean up resources.
     */
    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    /**
     * Checks if blocking is currently active based on the user's schedule.
     *
     * @return `true` if blocking is active; `false` otherwise.
     */
    private suspend fun isBlockingActive(): Boolean {
        val activeSchedule = FocusFortressApp.database.scheduleDao().getActiveSchedule().firstOrNull()
            ?: return false

        val calendar = Calendar.getInstance()
        val currentDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())?.uppercase()
        val currentTime = String.format(Locale.US, "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

        val daysOfWeek = activeSchedule.daysOfWeek.split(",")
        val startTime = activeSchedule.startTime
        val endTime = activeSchedule.endTime

        val isDayMatching = currentDay in daysOfWeek

        val isTimeMatching = if (startTime <= endTime) {
            // time - midnight not crossing
            currentTime in startTime..endTime
        } else {
            // time - midnight crossing
            currentTime >= startTime || currentTime <= endTime
        }

        return isDayMatching && isTimeMatching
    }

    /**
     * Called when an accessibility event is received.
     *
     * @param event The [AccessibilityEvent] received.
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val eventType = event.eventType
        val packageName = event.packageName?.toString() ?: return

        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {

                val browserConfig = supportedBrowsers.find { it.packageName == packageName }
                if (browserConfig != null) {
                    handleWebsiteBlocking(event, browserConfig)
                    handleKeywordBlocking(event, browserConfig)
                } else {
                    handleAppBlocking(event)
                }
            }
            else -> {
                // We do not handle other event types
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

        if (packageName == this.packageName) return
        if (packageName == lastBlockedPackageName && System.currentTimeMillis() - lastBlockedTimestamp < 3000) return

        // coroutine to check if the app is blocked, changed to scope from runBlocking due to main thread blocking
        serviceScope.launch {
            if (!isBlockingActive()) return@launch // must be run in coroutine to not block main thread!!
            // database query
            val isBlocked = FocusFortressApp.database.blockedAppDao().isAppBlocked(packageName)
            if (isBlocked) {
                withContext(Dispatchers.Main) {

                    lastBlockedPackageName = packageName
                    lastBlockedTimestamp = System.currentTimeMillis()

                    // foreground popup
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
        val source = event.source ?: return
        val url = captureUrl(source, browserConfig)

        if (url != null) {
            val domain = extractDomain(url)?.lowercase(Locale.getDefault())?.removePrefix("www.")

            if (domain != null) {

                if (domain == lastBlockedDomain && System.currentTimeMillis() - lastBlockedTimestamp < 500) return

                serviceScope.launch {
                    if (!isBlockingActive()) return@launch
                    val isBlocked = FocusFortressApp.database.blockedWebsiteDao().isWebsiteBlocked(domain)
                    if (isBlocked) {
                        withContext(Dispatchers.Main) {

                            lastBlockedDomain = domain
                            lastBlockedTimestamp = System.currentTimeMillis()

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

        if (packageName == this.packageName) return

        val source = event.source ?: return
        val url = captureUrl(source, browserConfig)

        if (url != null) {
            val searchText = extractSearchTextFromUrl(url)
            if (searchText != null) {
                handleKeywordBlockingHelper(searchText, browserConfig)
            }
        } else {
            val eventText = event.text.joinToString(" ")
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
        if (searchText.isEmpty()) return

        serviceScope.launch {
            if (!isBlockingActive()) return@launch
            val blockedKeywords = FocusFortressApp.database.blockedKeywordDao().getBlockedKeywords().firstOrNull() ?: return@launch
            val normalizedQuery = searchText.lowercase(Locale.getDefault())

            val matchedKeyword = blockedKeywords.find { keyword ->
                normalizedQuery.contains(keyword.keyword, ignoreCase = true)
            }

            if (matchedKeyword != null) {
                withContext(Dispatchers.Main) {
                    if (matchedKeyword.keyword == lastBlockedKeyword &&
                        System.currentTimeMillis() - lastBlockedTimestamp < 3000
                    ) return@withContext

                    lastBlockedKeyword = matchedKeyword.keyword
                    lastBlockedTimestamp = System.currentTimeMillis()

                    val intent = Intent(this@BlockerService, BlockedActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra("BLOCK_TYPE", "keyword")
                        putExtra("BROWSER_PACKAGE", browserConfig.packageName)
                        putExtra("BLOCKED_KEYWORD", matchedKeyword.keyword)
                    }
                    startActivity(intent)
                }
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
        try {
            val nodes = info.findAccessibilityNodeInfosByViewId(browserConfig.addressBarId)
            if (nodes.isNullOrEmpty()) {
                return null
            }
            val url = nodes[0].text?.toString()
            return url
        } catch (e: Exception) {
            return null
        }
    }
}