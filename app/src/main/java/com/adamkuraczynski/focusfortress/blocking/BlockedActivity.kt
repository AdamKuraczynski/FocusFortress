package com.adamkuraczynski.focusfortress.blocking

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.adamkuraczynski.focusfortress.ui.theme.FocusFortressTheme

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

    private fun goToHomeScreen() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

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