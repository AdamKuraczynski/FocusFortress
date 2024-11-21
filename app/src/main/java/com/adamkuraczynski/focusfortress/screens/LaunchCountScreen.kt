package com.adamkuraczynski.focusfortress.screens

import com.adamkuraczynski.focusfortress.service.AppLaunchCount
import com.adamkuraczynski.focusfortress.service.LaunchCountViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
 * Displays the app launch count screen, where users can view app launch counts
 * over different time periods ("Day", "3 Days", "Week").
 *
 * This composable function retrieves app launch data from the `LaunchCountViewModel` and
 * presents it in a scrollable list, allowing users to toggle between different time periods.
 * The screen also includes a styled top bar with navigation controls and period selection options.
 *
 * @author Adam Kuraczyński
 * @version 1.3
 *
 * @param navController The [NavController] used to navigate between app screens.
 * @param viewModel The [LaunchCountViewModel] providing app launch data.
 *
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchCountScreen(
    navController: NavController,
    viewModel: LaunchCountViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val backgroundImage = painterResource(id = R.drawable.barn)

    var selectedPeriod by remember { mutableStateOf("Day") }
    val appLaunchCounts by viewModel.appLaunchCounts.collectAsState()

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
                                "Launch Count",
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
                                viewModel.loadAppLaunchCounts(selectedPeriod)
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
                    items(appLaunchCounts) { app ->
                        AppLaunchItem(app)
                    }
                }
            }
        }
    )
}

/**
 * Displays a single app launch item in the list.
 *
 * This composable function shows the app's icon, name, and launch count.
 *
 * @param app The [AppLaunchCount] object containing the app's launch details.
 *
 */

@Composable
fun AppLaunchItem(app: AppLaunchCount) {
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
            text = app.launchCount.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = MedievalFont,
                fontSize = 20.sp,
                color = Color(0xFFE0C097)
            )
        )
    }
}
