package com.adamkuraczynski.focusfortress.blocking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont

@Composable
fun BlockedScreen(blockType: String, onExit: () -> Unit) {
    val message = if (blockType == "app") "App blocked by" else "Website blocked by"
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = MedievalFont
                )
            )
            Text(
                text = "Focus Fortress",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Golden,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = MedievalFont
                )
            )

            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = "Focus Fortress Logo",
                modifier = Modifier.size(100.dp)
            )

            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Golden,
                    contentColor = Color.Black
                ),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "Exit",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp,
                        fontFamily = MedievalFont
                    )
                )
            }
        }
    }
}