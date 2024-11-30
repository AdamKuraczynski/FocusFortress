package com.adamkuraczynski.focusfortress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/**
 * A composable function that displays an option item with a description and selection state.
 *
 * This component is used to display options in a list where users can select one.
 * It visually indicates the selected state and handles user interactions.
 *
 * @param description The description text for the option.
 * @param isSelected Indicates whether the option is currently selected.
 * @param onSelect Callback function invoked when the option is selected.
 * @param shape The shape of the option item. Defaults to [RoundedCornerShape] with 16.dp radius.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.3
 *
 * @see androidx.compose.material3.Card
 * @see androidx.compose.foundation.clickable
 */
@Composable
fun OptionItem(
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    shape: Shape = RoundedCornerShape(16.dp)
) {
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
                .clickable { onSelect() }
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = if (isSelected) listOf(Golden, LightGolden) else listOf(LightBrown, DarkBrown)
                    ),
                    shape = shape
                )
                .clip(shape)
                .background(
                    color = if (isSelected) Golden.copy(alpha = 0.2f) else LightBrown,
                    shape = shape
                )
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightBrown
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = description,
                    style = TextStyle(
                        fontFamily = MedievalFont,
                        fontSize = 20.sp,
                        color = if (isSelected) Golden else Color.White,
                        textAlign = TextAlign.Start
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Chosen Option",
                        tint = Golden,
                    )
                }
            }
        }
    }
}