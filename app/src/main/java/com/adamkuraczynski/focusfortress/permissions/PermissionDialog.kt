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
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont

/**
 * Displays a dialog prompting the user to grant a permission.
 *
 * This composable function shows an alert dialog with a title, description,
 * and two buttons: "OK" and "Cancel". The dialog is intended to inform the user
 * about a required permission and prompt them to take action.
 *
 * @author Adam Kuraczyński
 * @version 1.2
 *
 * @param title The title of the dialog
 * @param description The description of the dialog
 * @param onDismiss The action to perform when the dialog is dismissed
 * @param onOkClick The action to perform when the user clicks the OK button
 * @param modifier The modifier to apply to the dialog
 *
 **/

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
                modifier = Modifier.background(Color(0xFF6D4C41))
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
                modifier = Modifier.background(Color(0xFF6D4C41))
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
        containerColor = Color(0xFF2F2D38),
        modifier = modifier.background(Color(0xFF3B2F2F))

    )
}