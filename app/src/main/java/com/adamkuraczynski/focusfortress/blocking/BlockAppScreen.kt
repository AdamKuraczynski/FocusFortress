package com.adamkuraczynski.focusfortress.blocking

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.database.BlockedApp
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockAppScreen(
    navController: NavController,
    viewModel: BlockAppViewModel = viewModel(factory = BlockAppViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val backgroundImage = painterResource(id = R.drawable.fireplace)
    val installedApps by viewModel.installedApps.collectAsState()
    val blockedApps by viewModel.blockedApps.collectAsState()
    var searchText by rememberSaveable { mutableStateOf("") }
    var showOnlyBlocked by rememberSaveable { mutableStateOf(false) }

    // not in viewmodel because it is a ui state
    val filteredApps = remember(installedApps, blockedApps, showOnlyBlocked, searchText) {
        installedApps
            .let { apps ->
                if (showOnlyBlocked) {
                    val blockedPackageNames = blockedApps.map { it.packageName }
                    apps.filter { it.packageName in blockedPackageNames }
                } else {
                    apps
                }
            }
            .filter { it.appName.contains(searchText, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Block App",
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
                        containerColor = Color.Black,
                    ),
                    modifier = Modifier.background(Color.Transparent)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3B2F2F), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = {
                            Text(
                                text = "Search apps...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = MedievalFont,
                                    fontSize = 16.sp,
                                    color = Color(0xFFE0C097)
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .border(
                                width = 3.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = MedievalFont,
                            color = Color.White
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF3B2F2F),
                            unfocusedContainerColor = Color(0xFF3B2F2F),
                            disabledContainerColor = Color(0xFF3B2F2F),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        )
                    )

                    Checkbox(
                        checked = showOnlyBlocked,
                        onCheckedChange = { showOnlyBlocked = it },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = Color(0xFFE0C097),
                            uncheckedColor = Color(0xFF6D4C41),
                            checkedColor = Color(0xFF6D4C41)
                        )
                    )
                    Text(
                        text = "Blocked",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = MedievalFont,
                            fontSize = 16.sp,
                            color = Golden
                        )
                    )
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
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


                if (installedApps.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {

                        if (filteredApps.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .background(Color(0xFF6D4C41), shape = RoundedCornerShape(8.dp))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        "No matching apps",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontFamily = MedievalFont,
                                            fontSize = 20.sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = filteredApps,
                                    key = { it.packageName }
                                ) { appInfo ->
                                    val isBlocked = blockedApps.any { it.packageName == appInfo.packageName }

                                    AppItem(
                                        appInfo = appInfo,
                                        isBlocked = isBlocked,
                                        onToggleBlock = {
                                            if (isBlocked) {
                                                viewModel.unblockApp(
                                                    BlockedApp(
                                                        appInfo.packageName,
                                                        appInfo.appName
                                                    )
                                                )
                                            } else {
                                                viewModel.blockApp(appInfo.packageName, appInfo.appName)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun AppItem(appInfo: AppInfo, isBlocked: Boolean, onToggleBlock: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleBlock() }
            .padding(vertical = 8.dp)
            .background(Color(0xFF6D4C41), shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = remember { BitmapPainter(appInfo.appIcon.toBitmap().asImageBitmap()) },
            contentDescription = appInfo.appName,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = appInfo.appName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = MedievalFont,
                fontSize = 20.sp,
                color = Color.White
            ),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = if (isBlocked) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = if (isBlocked) "Blocked" else "Not Blocked",
            tint = Golden
        )
    }
}
