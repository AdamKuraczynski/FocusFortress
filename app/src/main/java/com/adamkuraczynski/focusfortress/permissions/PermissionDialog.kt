package com.adamkuraczynski.focusfortress.permissions

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
            Text(text = title)
        },
        text = {
            Text(text = description)
        },
        confirmButton = {
            TextButton(onClick = onOkClick
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
    )
}