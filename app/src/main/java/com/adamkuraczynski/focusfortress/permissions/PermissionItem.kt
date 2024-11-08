package com.adamkuraczynski.focusfortress.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont

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
    shape: Shape = RoundedCornerShape(16.dp)
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        PermissionDialog(
            title = title,
            description = textProvider.getDialogDescription(),
            onDismiss = { showDialog = false },
            onOkClick = {
                showDialog = false
                onClick()
            },
            modifier = modifier.background(Color(0xFF3B2F2F), shape = shape)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF3B2F2F))
            .padding(4.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, shape)
                .clickable {
                    if (!granted) showDialog = true
                }
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = if (granted) listOf(Golden, Color(0xFFD4A373)) else listOf(Color(0xFF6D4C41), Color(0xFF3B2F2F))
                    ),
                    shape = shape
                )
                .clip(shape)
                .background(
                    color = if (granted) Golden.copy(alpha = 0.2f) else Color(0xFF6D4C41),
                    shape = shape
                )
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF6D4C41)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontFamily = MedievalFont,
                            fontSize = 20.sp,
                            color = if (granted) Golden else Color.White,
                            textAlign = TextAlign.Start
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = textProvider.getItemDescription(),
                        style = TextStyle(
                            fontFamily = MedievalFont,
                            fontSize = 14.sp,
                            color = Color(0xFFB0A191)
                        )
                    )
                }
                if (granted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Granted",
                        tint = Golden
                    )
                }
            }
        }
    }
}