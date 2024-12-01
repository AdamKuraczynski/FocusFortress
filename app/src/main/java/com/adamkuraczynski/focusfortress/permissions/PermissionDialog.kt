package com.adamkuraczynski.focusfortress.permissions

import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.adamkuraczynski.focusfortress.ui.theme.DarkBrown
import com.adamkuraczynski.focusfortress.ui.theme.DarkGray
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.LightBrown
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont


@Composable
fun PermissionDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = MedievalFont,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = Golden
                )
            )
        },
        text = {
            Text(
                text = description,
                style = TextStyle(
                    fontFamily = MedievalFont,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = Color.White
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = onOkClick,
                modifier = Modifier.background(LightBrown)
            ) {
                Text(
                    "OK",
                    style = TextStyle(
                        fontFamily = MedievalFont,
                        color = Color.White,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.background(LightBrown)
            ) {
                Text(
                    "Cancel",
                    style = TextStyle(
                        fontFamily = MedievalFont,
                        color = Color.White,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                )
            }
        },
        containerColor = DarkGray,
        modifier = modifier.background(DarkBrown)
    )
}