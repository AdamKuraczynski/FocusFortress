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

class BlockerService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val supportedBrowsers = listOf(
        SupportedBrowserConfig("com.android.chrome", "com.android.chrome:id/url_bar"),
        SupportedBrowserConfig("org.mozilla.firefox", "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"),
    )
    private var lastBlockedPackageName: String? = null
    private var lastBlockedDomain: String? = null
    private var lastBlockedKeyword: String? = null
    private var lastBlockedTimestamp: Long = 0

    data class SupportedBrowserConfig(
        val packageName: String,
        val addressBarId: String
    )

    override fun onInterrupt() {
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

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
        }
    }

    private fun handleAppBlocking(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return

        if (packageName == this.packageName) return
        if (packageName == lastBlockedPackageName && System.currentTimeMillis() - lastBlockedTimestamp < 3000) return

        // coroutine to check if the app is blocked, changed to scope from runBlocking due to main thread blocking
        serviceScope.launch {
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

    private fun handleWebsiteBlocking(event: AccessibilityEvent, browserConfig: SupportedBrowserConfig) {
        val source = event.source ?: return
        val url = captureUrl(source, browserConfig)
        source.recycle()

        if (url != null) {
            val domain = extractDomain(url)?.lowercase(Locale.getDefault())?.removePrefix("www.")

            if (domain != null) {

                if (domain == lastBlockedDomain && System.currentTimeMillis() - lastBlockedTimestamp < 500) return

                serviceScope.launch {
                    val isBlocked = FocusFortressApp.database.blockedWebsiteDao().isWebsiteBlocked(domain)
                    if (isBlocked) {
                        withContext(Dispatchers.Main) {

                            lastBlockedDomain = domain
                            lastBlockedTimestamp = System.currentTimeMillis()

                            val intent = Intent(this@BlockerService, BlockedActivity::class.java) .apply {
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

    private fun handleKeywordBlocking(event: AccessibilityEvent, browserConfig: SupportedBrowserConfig) {
        val packageName = event.packageName?.toString() ?: return

        if (packageName == this.packageName) return

        val source = event.source ?: return
        val url = captureUrl(source, browserConfig)
        source.recycle()

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

    private fun handleKeywordBlockingHelper(searchText: String, browserConfig: SupportedBrowserConfig) {
        if (searchText.isEmpty()) return

        serviceScope.launch {
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

                    val intent = Intent(this@BlockerService, BlockedActivity::class.java) .apply {
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

    private fun captureUrl(info: AccessibilityNodeInfo, browserConfig: SupportedBrowserConfig): String? {
        try {
            val nodes = info.findAccessibilityNodeInfosByViewId(browserConfig.addressBarId)
            if (nodes.isNullOrEmpty()) {
                return null
            }
            val url = nodes[0].text?.toString()
            for (node in nodes) {
                node.recycle()
            }
            return url
        } catch (e: Exception) {
            return null
        }
    }
}
