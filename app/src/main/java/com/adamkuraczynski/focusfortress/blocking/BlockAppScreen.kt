package com.adamkuraczynski.focusfortress.blocking

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.database.BlockedApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockAppScreen(
    navController: NavController,
    viewModel: BlockAppViewModel = viewModel(factory = BlockAppViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val backgroundImage = painterResource(id = R.drawable.fireplace)
    val installedApps by viewModel.installedApps.collectAsState()
    val blockedApps by viewModel.blockedApps.collectAsState()
    var searchText by rememberSaveable  { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Block App") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            if (installedApps.isEmpty()) {
                // nice loading spinner
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Box {
                    Image(
                        painter = backgroundImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Column(modifier = Modifier.fillMaxSize()) {
                        BasicTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingValues),
                            decorationBox = { innerTextField ->
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            MaterialTheme.shapes.small
                                        )
                                        .padding(8.dp)
                                ) {
                                    if (searchText.isEmpty()) {
                                        Text(
                                            "Search apps...",
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        val filteredApps = remember(installedApps, searchText) {
                            installedApps.filter {
                                it.appName.contains(searchText, ignoreCase = true)
                            }
                        }

                        LazyColumn {
                            items(
                                items = filteredApps,
                                key = {
                                    it.packageName
                                }
                            ) { appInfo ->
                                val isBlocked =
                                    blockedApps.any { it.packageName == appInfo.packageName }

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
    )

}

@Composable
fun AppItem(appInfo: AppInfo, isBlocked: Boolean, onToggleBlock: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleBlock() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = remember { BitmapPainter(appInfo.appIcon.toBitmap().asImageBitmap()) },
            contentDescription = appInfo.appName,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = appInfo.appName,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            imageVector = if (isBlocked) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = if (isBlocked) "Blocked" else "Not Blocked"
        )
    }
}