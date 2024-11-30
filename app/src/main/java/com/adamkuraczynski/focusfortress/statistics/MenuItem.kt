package com.adamkuraczynski.focusfortress.statistics

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont

/**
 * Composable function for a reusable dropdown menu item.
 *
 * This function creates a styled menu item that can be used in dropdown menus
 * throughout the application.
 *
 * @param text The text to display in the menu item.
 * @param onClick The action to execute when the menu item is clicked.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.2
 *
 * @see androidx.compose.material3.DropdownMenuItem
 * @see androidx.compose.material3.Text
 */
@Composable
fun MenuItem(
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        onClick = onClick,
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = MedievalFont,
                    fontSize = 16.sp,
                    color = Color.White
                )
            )
        }
    )
}