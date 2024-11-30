package com.adamkuraczynski.focusfortress.blocking

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.adamkuraczynski.focusfortress.ui.theme.FocusFortressTheme

/**
 * Activity that is displayed when a blocked app, website, or keyword is accessed.
 *
 * This activity prevents the user from accessing the blocked content and provides
 * an option to exit back to the home screen or a safe location.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.4
 *
 * @see androidx.activity.ComponentActivity
 */
class BlockedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToHomeScreen()
            }
        })

        val blockType = intent.getStringExtra("BLOCK_TYPE") ?: "app"
        val browserPackage = intent.getStringExtra("BROWSER_PACKAGE")

        setContent {
            FocusFortressTheme {
                BlockedScreen(
                    blockType = blockType,
                    onExit = {
                        handleExit(blockType, browserPackage)
                    }
                )
            }
        }

    }

    /**
     * Navigates the user to the home screen.
     */
    private fun goToHomeScreen() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    /**
     * Opens a new tab in the specified browser.
     *
     * @param browserPackage The package name of the browser.
     */
    private fun openNewTabInBrowser(browserPackage: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                setPackage(browserPackage)
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            goToHomeScreen()
        } catch (e: Exception) {
            goToHomeScreen()
        }
    }

    /**
     * Handles the exit action based on the block type.
     *
     * @param blockType The type of content that was blocked ("app", "website", "keyword").
     * @param browserPackage The package name of the browser, if applicable.
     */
    private fun handleExit(blockType: String = "app", browserPackage: String? = null) {
        when (blockType) {
            "website", "keyword" -> {
                if (browserPackage != null) {
                    openNewTabInBrowser(browserPackage)
                } else {
                    goToHomeScreen()
                }
            }
            "app" -> {
                goToHomeScreen()
            }
        }
        finish()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

}