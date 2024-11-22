package com.adamkuraczynski.focusfortress.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent
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


class BlockerService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var lastBlockedPackageName: String? = null
    private var lastBlockedDomain: String? = null
    private var lastBlockedTimestamp: Long = 0

    data class SupportedBrowserConfig(
        val packageName: String,
        val addressBarId: String
    )

    private val supportedBrowsers = listOf(
        SupportedBrowserConfig("com.android.chrome", "com.android.chrome:id/url_bar"),
        SupportedBrowserConfig("org.mozilla.firefox", "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"),
    )

    override fun onCreate() {
        super.onCreate()
        Log.d("BlockerService", "Service Created")
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("BlockerService", "Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d("BlockerService", "Accessibility Event Received: ${event?.eventType}")
        if (event == null) return

        val eventType = event.eventType
        val packageName = event.packageName?.toString() ?: return

        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOWS_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {

                val browserConfig = supportedBrowsers.find { it.packageName == packageName }
                if (browserConfig != null) {
                    handleBrowserEvent(event, browserConfig)
                } else {
                    handleAppEvent(event)
                }
            }
        }
    }

    private fun handleAppEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        Log.d("BlockerService", "App Launched: $packageName")

        if (packageName == this.packageName) {
            Log.d("BlockerService", "Ignoring events from own app: $packageName")
            return
        }

        if (packageName == lastBlockedPackageName && System.currentTimeMillis() - lastBlockedTimestamp < 3000) {
            Log.d("BlockerService", "Skipping duplicate block for app $packageName")
            return
        }

        // coroutine to check if the app is blocked, changed to scope from runBlocking due to main thread blocking
        serviceScope.launch {
            try {
                // database query
                val isBlocked = FocusFortressApp.database.blockedAppDao().isAppBlocked(packageName)
                if (isBlocked) {
                    withContext(Dispatchers.Main) {
                        Log.d("BlockerService", "Blocking app: $packageName")

                        lastBlockedPackageName = packageName
                        lastBlockedTimestamp = System.currentTimeMillis()

                        // foreground popup
                        val intent = Intent(this@BlockerService, BlockedActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("BLOCK_TYPE", "app")
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                Log.e("BlockerService", "Error in handleAppEvent: ${e.message}", e)
            }
        }
    }

    private fun handleBrowserEvent(event: AccessibilityEvent, browserConfig: SupportedBrowserConfig) {
        val source = event.source ?: return
        val url = captureUrl(source, browserConfig)
        source.recycle()

        Log.d("BlockerService", "Captured URL: $url")

        if (url != null) {
            val domain = extractDomain(url)?.lowercase(Locale.getDefault())?.removePrefix("www.")
            Log.d("BlockerService", "Extracted domain: $domain")

            if (domain != null) {

                if (domain == lastBlockedDomain && System.currentTimeMillis() - lastBlockedTimestamp < 500) {
                    Log.d("BlockerService", "Skipping duplicate block for domain $domain")
                    return
                }

                serviceScope.launch {
                    try {
                        val isBlocked = FocusFortressApp.database.blockedWebsiteDao().isWebsiteBlocked(domain)
                        Log.d("BlockerService", "Is domain blocked: $isBlocked")
                        if (isBlocked) {
                            withContext(Dispatchers.Main) {
                                Log.d("BlockerService", "Blocking website: $domain")

                                lastBlockedDomain = domain
                                lastBlockedTimestamp = System.currentTimeMillis()

                                val intent = Intent(this@BlockerService, BlockedActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("BLOCK_TYPE", "website")
                                startActivity(intent)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("BlockerService", "Error in handleBrowserEvent: ${e.message}", e)
                    }
                }
            } else {
                Log.d("BlockerService", "Domain is null after extraction.")
            }
        } else {
            Log.d("BlockerService", "URL is null.")
        }
    }


    private fun captureUrl(info: AccessibilityNodeInfo, browserConfig: SupportedBrowserConfig): String? {
       try {
           val nodes = info.findAccessibilityNodeInfosByViewId(browserConfig.addressBarId)
           if (nodes.isNullOrEmpty()) {
               Log.d("BlockerService", "No nodes found for address bar ID: ${browserConfig.addressBarId}")
               return null
           }
           val url = nodes[0].text?.toString()
           for (node in nodes) {
               node.recycle()
           }
           Log.d("BlockerService", "Address bar text: $url")
           return url
       } catch (e: Exception) {
           Log.e("BlockerService", "Error in captureUrl: ${e.message}", e)
           return null
       }

    }

    private fun extractDomain(url: String): String? {
        return try {
            var fixedUrl = url
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                fixedUrl = "https://$url"
            }
            val uri = android.net.Uri.parse(fixedUrl)
            val domain = uri.host?.lowercase(Locale.getDefault())?.removePrefix("www.")
            domain
        } catch (e: Exception) {
            Log.e("BlockerService", "Error in extractDomain: ${e.message}", e)
            null
        }
    }

    override fun onInterrupt() {
        Log.d("BlockerService", "Service Interrupted")
    }
}
