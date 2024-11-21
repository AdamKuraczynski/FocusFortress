package com.adamkuraczynski.focusfortress.screens

import android.annotation.SuppressLint
import com.adamkuraczynski.focusfortress.service.AppUsage
import com.adamkuraczynski.focusfortress.service.ScreenTimeViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont


/**
 * Displays the screen time usage for apps over different time periods.
 *
 * This composable function retrieves app usage data from the `ScreenTimeViewModel` and
 * presents it in a scrollable list, allowing users to toggle between different time periods.
 * The screen includes a styled top bar with navigation controls and period selection options.
 *
 * @author Adam Kuraczyński
 * @version 1.4
 *
 * @param navController The [NavController] used to navigate between app screens.
 * @param viewModel The [ScreenTimeViewModel] providing app usage data.
 *
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTimeScreen(
    navController: NavController,
    viewModel: ScreenTimeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val backgroundImage = painterResource(id = R.drawable.stables)

    var selectedPeriod by remember { mutableStateOf("Day") }
    val appUsageTimes by viewModel.appUsageTimes.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Screen Time",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color(0xFFE0C097),
                                    fontFamily = MedievalFont,
                                    fontSize = 40.sp,
                                    shadow = Shadow(
                                        color = Color.Black,
                                        offset = Offset(2f, 2f),
                                        blurRadius = 4f
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier.background(Color.Transparent)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3B2F2F))
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val periods = listOf("Day", "3 Days", "Week")
                    periods.forEach { period ->
                        TextButton(
                            onClick = {
                                selectedPeriod = period
                                viewModel.loadAppUsageTimes(selectedPeriod)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (selectedPeriod == period) Color(0xFFE0C097) else Color.White
                            )
                        ) {
                            Text(
                                text = period,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = MedievalFont,
                                    fontSize = 18.sp,
                                    color = if (selectedPeriod == period) Color(0xFFE0C097) else Color.White
                                )
                            )
                        }
                    }
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Image(
                    painter = backgroundImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(appUsageTimes) { app ->
                        AppUsageItem(app)
                    }
                }
            }
        }
    )
}

/**
 * Displays a single app usage item in the list.
 *
 * This composable function shows the app's icon, name, and total usage time formatted as hours, minutes, and seconds.
 *
 * @param app The [AppUsage] object containing the app's usage details.
 *
 */
@Composable
fun AppUsageItem(app: AppUsage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFF6D4C41), shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (app.appIcon != null) {
            Image(
                bitmap = app.appIcon.toBitmap().asImageBitmap(),
                contentDescription = null,
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
        Text(
            text = app.appName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = MedievalFont,
                fontSize = 20.sp,
                color = Color.White
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = formatTime(app.usageTimeMillis),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = MedievalFont,
                fontSize = 20.sp,
                color = Color(0xFFE0C097)
            )
        )
    }
}

/**
 * Formats time in milliseconds to a string in the format "HH:mm:ss".
 *
 * @param timeMillis The time in milliseconds.
 * @return A formatted time string.
 *
 */
@SuppressLint("DefaultLocale")
fun formatTime(timeMillis: Long): String {
    val totalSeconds = timeMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}