package com.adamkuraczynski.focusfortress.blocking

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.database.BlockedApp
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockAppScreen(viewModel: BlockAppViewModel = viewModel(), navController: NavController) {
    val backgroundImage = painterResource(id = R.drawable.fireplace)
    val context = LocalContext.current
    val packageManager = context.packageManager

    var installedApps by remember { mutableStateOf(listOf<ApplicationInfo>()) }
    var blockedApps by remember { mutableStateOf(emptyList<BlockedApp>()) }
    var searchText by remember { mutableStateOf("") }

    // Fetch installed apps once
    LaunchedEffect(Unit) {
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        installedApps = packages.filter {
            packageManager.getLaunchIntentForPackage(it.packageName) != null
        }
    }

    // Observe blocked apps
    LaunchedEffect(Unit) {
        viewModel.blockedApps.collectLatest { blockedList ->
            blockedApps = blockedList
        }
    }

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
            Box{
                Image(
                    painter = backgroundImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column(modifier = Modifier.fillMaxSize()) {
                    // Search Bar
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
                                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
                                    .padding(8.dp)
                            ) {
                                if (searchText.isEmpty()) {
                                    Text("Search apps...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                                innerTextField()
                            }
                        }
                    )

                    // List of Apps
                    val filteredApps = installedApps.filter {
                        val appName = packageManager.getApplicationLabel(it).toString()
                        appName.contains(searchText, ignoreCase = true)
                    }

                    LazyColumn {
                        items(filteredApps) { appInfo ->
                            val appName = packageManager.getApplicationLabel(appInfo).toString()
                            val packageName = appInfo.packageName
                            val isBlocked = blockedApps.any { it.packageName == packageName }

                            AppItem(
                                appName = appName,
                                isBlocked = isBlocked,
                                onToggleBlock = {
                                    if (isBlocked) {
                                        viewModel.unblockApp(BlockedApp(packageName, appName))
                                    } else {
                                        viewModel.blockApp(packageName, appName)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    )

}

@Composable
fun AppItem(appName: String, isBlocked: Boolean, onToggleBlock: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleBlock() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = appName,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            imageVector = if (isBlocked) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = if (isBlocked) "Blocked" else "Not Blocked"
        )
    }
}