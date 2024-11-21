package com.adamkuraczynski.focusfortress.permissions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.ui.theme.DarkBrown
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont

/**
 * Displays the permission screen where the user can grant necessary permissions.
 *
 * This composable function checks if all required permissions are granted.
 * If they are, it automatically invokes the `onPermissionsGranted` callback to navigate to the main content.
 * If not, it presents a list of permissions that the user needs to grant, along with options to request them.
 *
 * @author Adam Kuraczyński
 * @version 1.5
 *
 * @param viewModel The [PermissionViewModel] that handles the permission logic and state.
 * @param onPermissionsGranted A callback function invoked when all permissions have been granted.
 *
 **/

@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel,
    onPermissionsGranted: () -> Unit
) {
    val hasUsageAccessPermission by viewModel.hasUsageAccessPermission.collectAsState()
    val hasOverlayPermission by viewModel.hasOverlayPermission.collectAsState()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()

    val allPermissionsGranted = hasUsageAccessPermission && hasOverlayPermission && hasNotificationPermission

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
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.gate),
                contentDescription = "Background image",
                contentScale = ContentScale.Crop, //fit always
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Required Permissions",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = MedievalFont,
                        color = Golden,
                        fontWeight = FontWeight.Bold,
                        fontSize = 44.sp
                    ),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                PermissionItem(
                    title = "Usage Access",
                    textProvider = UsageAccessPermissionTextProvider(),
                    granted = hasUsageAccessPermission,
                    onClick = { viewModel.requestUsageAccessPermission() },
                    modifier = Modifier.background(DarkBrown)
                )

                Spacer(modifier = Modifier.height(8.dp))

                PermissionItem(
                    title = "Display Over Other Apps",
                    textProvider = OverlayPermissionTextProvider(),
                    granted = hasOverlayPermission,
                    onClick = { viewModel.requestOverlayPermission() },
                    modifier = Modifier.background(DarkBrown)
                )

                Spacer(modifier = Modifier.height(8.dp))

                PermissionItem(
                    title = "Notification Access",
                    textProvider = NotificationPermissionTextProvider(),
                    granted = hasNotificationPermission,
                    onClick = { viewModel.requestNotificationAccess() },
                    modifier = Modifier.background(DarkBrown)
                )
            }
        }
    }
}