package com.adamkuraczynski.focusfortress.blocking

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.adamkuraczynski.focusfortress.ui.theme.FocusFortressTheme

class BlockedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val blockType = intent.getStringExtra("BLOCK_TYPE") ?: "app"
        setContent {
            FocusFortressTheme {
                BlockedScreen(
                    blockType = blockType,
                    onExit = {
                        goToHomeScreen()
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
        finish()
    }
}