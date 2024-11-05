package com.adamkuraczynski.focusfortress.permissions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Represents a single permission item in the UI,
 * allowing the user to interact with it to grant permissions.
 *
 * This composable displays a card with the title and description
 * of a required permission. When the permission is not granted,
 * clicking on the item shows a dialog prompting the user to take action.
 * When the permission is granted, a checkmark icon is displayed.
 *
 * @author Adam Kuraczyński
 * @version 1.4
 *
 * @param title The title of the permission item.
 * @param textProvider The text provider for the permission item.
 * @param granted Indicates whether the permission has already been granted.
 * @param onClick The action to perform when the user interacts with the permission item, typically to request the permission.
 * @param modifier The modifier to apply to the item for layout adjustments.
 *
 **/

@Composable
fun PermissionItem(
    title: String,
    textProvider: PermissionTextProvider,
    granted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        PermissionDialog(
            title = title,
            description = textProvider.getDialogDescription(),
            onDismiss = { showDialog = false },
            onOkClick = {
                showDialog = false
                onClick()
            },
            modifier = modifier
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (granted) return@clickable
                showDialog = true
            },
        colors = CardDefaults.cardColors(
            containerColor = if (granted) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = textProvider.getItemDescription(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (granted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
