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
import com.adamkuraczynski.focusfortress.ui.theme.DarkBrown
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.LightBrown
import com.adamkuraczynski.focusfortress.ui.theme.LightGolden
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont


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
            modifier = modifier.background(DarkBrown, shape = shape)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(DarkBrown)
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
                        colors = if (granted) listOf(Golden, LightGolden) else listOf(LightBrown, DarkBrown)
                    ),
                    shape = shape
                )
                .clip(shape)
                .background(
                    color = if (granted) Golden.copy(alpha = 0.2f) else LightBrown,
                    shape = shape
                )
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightBrown
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
                            color = Golden
                        )
                    )
                }
                if (granted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Permission Granted",
                        tint = Golden
                    )
                }
            }
        }
    }
}