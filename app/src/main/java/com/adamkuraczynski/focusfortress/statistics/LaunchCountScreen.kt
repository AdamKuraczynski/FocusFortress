package com.adamkuraczynski.focusfortress.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableIntStateOf
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
 * Displays the app launch count screen, where users can view app launch counts
 * over different time periods ("Day", "3 Days", "Week").
 *
 * This composable function retrieves app launch data from the `LaunchCountViewModel` and
 * presents it in a scrollable list, allowing users to toggle between different time periods.
 * The screen also includes a styled top bar with navigation controls and period selection options.
 *
 * @author Adam Kuraczyński
 * @version 1.5
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

    var selectedSortOptionLaunchCount by remember { mutableStateOf(SortOptionLaunchCount.LaunchCountDescending) }
    var minLaunchCount by remember { mutableIntStateOf(0) }

    // dropdown
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var isFilterMenuExpanded by remember { mutableStateOf(false) }

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
                        titleContentColor = Color.Black
                    ),
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
                                viewModel.loadAppLaunchCounts(selectedPeriod, selectedSortOptionLaunchCount, minLaunchCount)
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
                    Box{
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
                                text = when (selectedSortOptionLaunchCount) {
                                    SortOptionLaunchCount.LaunchCountDescending -> "Launch count ↓"
                                    SortOptionLaunchCount.LaunchCountAscending -> "Launch count ↑"
                                    SortOptionLaunchCount.AppNameAscending -> "App Name A→Z"
                                    SortOptionLaunchCount.AppNameDescending -> "App Name Z→A"
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
                            MenuItem("Launch Count ↓") {
                                selectedSortOptionLaunchCount = SortOptionLaunchCount.LaunchCountDescending
                                viewModel.loadAppLaunchCounts(selectedPeriod, selectedSortOptionLaunchCount, minLaunchCount)
                                isSortMenuExpanded = false
                            }
                            MenuItem("Launch Count ↑") {
                                selectedSortOptionLaunchCount = SortOptionLaunchCount.LaunchCountAscending
                                viewModel.loadAppLaunchCounts(selectedPeriod, selectedSortOptionLaunchCount, minLaunchCount)
                                isSortMenuExpanded = false
                            }
                            MenuItem("App Name A→Z") {
                                selectedSortOptionLaunchCount = SortOptionLaunchCount.AppNameAscending
                                viewModel.loadAppLaunchCounts(selectedPeriod, selectedSortOptionLaunchCount, minLaunchCount)
                                isSortMenuExpanded = false
                            }
                            MenuItem("App Name Z→A") {
                                selectedSortOptionLaunchCount = SortOptionLaunchCount.AppNameDescending
                                viewModel.loadAppLaunchCounts(selectedPeriod, selectedSortOptionLaunchCount, minLaunchCount)
                                isSortMenuExpanded = false
                            }
                        }
                    }

                    // filtering
                    Box{
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
                                text = "$minLaunchCount+ Launches",
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
                            listOf(0, 5, 10, 20).forEach { count ->
                                MenuItem("$count+ Launches") {
                                    minLaunchCount = count
                                    viewModel.loadAppLaunchCounts(selectedPeriod, selectedSortOptionLaunchCount, minLaunchCount)
                                    isFilterMenuExpanded = false
                                }
                            }
                        }
                    }
                }
            }
        },
        content = { paddingValues ->
            Box (
                modifier = Modifier
                    .fillMaxSize()
            ){
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(appLaunchCounts) { app ->
                            AppItem(app)
                        }
                    }
                }
            }
        }

    )
}