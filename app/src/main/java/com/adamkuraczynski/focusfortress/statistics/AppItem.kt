package com.adamkuraczynski.focusfortress.statistics

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.LightBrown
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont


@Composable
fun AppItem(app: Any) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(LightBrown, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val appIcon = when (app) {
            is AppLaunchCount -> app.appIcon
            is AppUsage -> app.appIcon
            else -> null
        }

        if (appIcon != null) {
            Image(
                bitmap = appIcon.toBitmap().asImageBitmap(),
                contentDescription = "$appIcon icon",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        val appName = when (app) {
            is AppLaunchCount -> app.appName
            is AppUsage -> app.appName
            else -> "Unknown App"
        }

        Text(
            text = appName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = MedievalFont,
                fontSize = 20.sp,
                color = Color.White
            ),
            modifier = Modifier.weight(1f)
        )

        val detailText by remember(app) {
            derivedStateOf {
                when (app) {
                    is AppLaunchCount -> app.launchCount.toString()
                    is AppUsage -> formatTime(app.usageTimeMillis)
                    else -> ""
                }
            }
        }

        Text(
            text = detailText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = MedievalFont,
                fontSize = 20.sp,
                color = Golden
            )
        )
    }
}


@SuppressLint("DefaultLocale")
fun formatTime(timeMillis: Long): String {
    val totalSeconds = timeMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}