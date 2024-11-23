package com.adamkuraczynski.focusfortress.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
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
 * @version 1.6
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

    var selectedSortOption by remember { mutableStateOf(SortOptionScreenTime.UsageTimeDescending) }
    var minUsageTimeMillis by remember { mutableLongStateOf(0L) }

    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var isFilterMenuExpanded by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()

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
                                viewModel.loadAppUsageTimes(selectedPeriod, selectedSortOption, minUsageTimeMillis)
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3B2F2F))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // sorting
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isSortMenuExpanded = !isSortMenuExpanded }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (selectedSortOption) {
                                    SortOptionScreenTime.UsageTimeDescending -> "Launch count ↓"
                                    SortOptionScreenTime.UsageTimeAscending -> "Launch count ↑"
                                    SortOptionScreenTime.AppNameAscending -> "App Name A→Z"
                                    SortOptionScreenTime.AppNameDescending -> "App Name Z→A"
                                },
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = MedievalFont,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            )
                        }
                        DropdownMenu(
                            expanded = isSortMenuExpanded,
                            onDismissRequest = { isSortMenuExpanded = false },
                        ) {
                            MenuItem("Usage Time ↓") {
                                selectedSortOption = SortOptionScreenTime.UsageTimeDescending
                                viewModel.loadAppUsageTimes(selectedPeriod, selectedSortOption, minUsageTimeMillis)
                                isSortMenuExpanded = false
                            }
                            MenuItem("Usage Time ↑") {
                                selectedSortOption = SortOptionScreenTime.UsageTimeAscending
                                viewModel.loadAppUsageTimes(selectedPeriod, selectedSortOption, minUsageTimeMillis)
                                isSortMenuExpanded = false
                            }
                            MenuItem("App Name A→Z") {
                                selectedSortOption = SortOptionScreenTime.AppNameAscending
                                viewModel.loadAppUsageTimes(selectedPeriod, selectedSortOption, minUsageTimeMillis)
                                isSortMenuExpanded = false
                            }
                            MenuItem("App Name Z→A") {
                                selectedSortOption = SortOptionScreenTime.AppNameDescending
                                viewModel.loadAppUsageTimes(selectedPeriod, selectedSortOption, minUsageTimeMillis)
                                isSortMenuExpanded = false
                            }
                        }
                    }

                    // filtering
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isFilterMenuExpanded = !isFilterMenuExpanded }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when {
                                    minUsageTimeMillis >= 3600000L -> "${minUsageTimeMillis / 3600000}h+ Usage"
                                    else -> "${minUsageTimeMillis / 60000}m+ Usage"
                                },
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = MedievalFont,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            )
                        }
                        DropdownMenu(
                            expanded = isFilterMenuExpanded,
                            onDismissRequest = { isFilterMenuExpanded = false },
                        ) {
                            // Options: 0m, 15m, 1h, 5h
                            listOf(0L, 900000L, 3600000L, 18000000L).forEach { time ->
                                MenuItem(
                                    text = when {
                                        time >= 3600000L -> "${time / 3600000}h+ Usage"
                                        else -> "${time / 60000}m+ Usage"
                                    }
                                ) {
                                    minUsageTimeMillis = time
                                    viewModel.loadAppUsageTimes(selectedPeriod, selectedSortOption, minUsageTimeMillis)
                                    isFilterMenuExpanded = false
                                }
                            }
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

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(
                            items = appUsageTimes,
                            key = {
                                it.appName
                            }
                        ) { app ->
                            AppItem(app)
                        }
                    }
                }
            }
        }
    )
}
