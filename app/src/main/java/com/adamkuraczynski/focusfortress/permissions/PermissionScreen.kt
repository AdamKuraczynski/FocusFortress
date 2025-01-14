package com.adamkuraczynski.focusfortress.permissions

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.ui.theme.Brown
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel,
    onPermissionsGranted: () -> Unit
) {
    val hasUsageAccessPermission by viewModel.hasUsageAccessPermission.collectAsState()
    val hasOverlayPermission by viewModel.hasOverlayPermission.collectAsState()
    val hasAccessibilityPermission by viewModel.hasAccessibilityPermission.collectAsState()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()

    val allPermissionsGranted = hasUsageAccessPermission && hasOverlayPermission && hasAccessibilityPermission && hasNotificationPermission

    // permissions update each time the screen is resumed
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.updatePermissionsStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    // observer
    LaunchedEffect(allPermissionsGranted) {
        if (allPermissionsGranted) {
            onPermissionsGranted()
        }
    }

    if (!allPermissionsGranted) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Required Permissions",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Golden,
                                fontFamily = MedievalFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 38.sp,
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 4f
                                ),
                                textAlign = TextAlign.Center
                            )
                        )
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Black,
                    ),
                    modifier = Modifier
                        .background(Color.Transparent)
                        .wrapContentHeight()
                )
            },
            content = { paddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gate),
                        contentDescription = "Background image of a castle gate",
                        contentScale = ContentScale.Crop, //fit always
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
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(modifier = Modifier.height(8.dp))

                        PermissionItem(
                            title = "Usage Access",
                            textProvider = UsageAccessPermissionTextProvider(),
                            granted = hasUsageAccessPermission,
                            onClick = { viewModel.requestUsageAccessPermission() },
                            modifier = Modifier.background(Brown)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PermissionItem(
                            title = "Overlay Access",
                            textProvider = OverlayPermissionTextProvider(),
                            granted = hasOverlayPermission,
                            onClick = { viewModel.requestOverlayPermission() },
                            modifier = Modifier.background(Brown)
                        )


                        // api 33+ needed permission request
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Spacer(modifier = Modifier.height(8.dp))

                            PermissionItem(
                                title = "Notification Access",
                                textProvider = NotificationPermissionTextProvider(),
                                granted = hasNotificationPermission,
                                onClick = { viewModel.requestNotificationPermission() },
                                modifier = Modifier.background(Brown)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        PermissionItem(
                            title = "Accessibility Access",
                            textProvider = AccessibilityPermissionTextProvider(),
                            granted = hasAccessibilityPermission,
                            onClick = { viewModel.requestPermissionAccess() },
                            modifier = Modifier.background(Brown)
                        )
                    }
                }
            }
        )
    }
}