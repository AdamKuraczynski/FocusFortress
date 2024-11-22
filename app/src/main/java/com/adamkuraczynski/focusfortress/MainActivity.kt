package com.adamkuraczynski.focusfortress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adamkuraczynski.focusfortress.screens.MainContent
import com.adamkuraczynski.focusfortress.permissions.PermissionScreen
import com.adamkuraczynski.focusfortress.permissions.PermissionViewModel
import com.adamkuraczynski.focusfortress.screens.BlockKeywordScreen
import com.adamkuraczynski.focusfortress.screens.BlockWebsiteScreen
import com.adamkuraczynski.focusfortress.screens.SelectStrictnessScreen
import com.adamkuraczynski.focusfortress.screens.TakeABreakScreen
import com.adamkuraczynski.focusfortress.statistics.LaunchCountScreen
import com.adamkuraczynski.focusfortress.statistics.ScreenTimeScreen
import com.adamkuraczynski.focusfortress.ui.theme.FocusFortressTheme
import com.adamkuraczynski.focusfortress.blocking.BlockAppScreen

/**
 * The main entry point of the FocusFortress application.
 *
 * This activity initializes the Jetpack Compose UI and sets up the main navigation graph.
 * It handles the navigation between the permission request screen and the main content screen,
 * ensuring that the app only proceeds to the main content if all required permissions are granted.
 *
 * **Navigation Flow:**
 * - If all permissions are granted, starts with the "main" screen.
 * - If any permissions are missing, starts with the "permissions" screen.
 * - Continuously monitors permission status and navigates accordingly.
 *
 * **Permission Handling:**
 * - Observes the permission status using a shared [PermissionViewModel].
 * - Uses lifecycle observers to update permission status when the app resumes.
 * - Automatically navigates to the permissions screen if permissions are revoked.
 *
 * @author Adam Kuraczyński
 * @version 1.8
 *
 **/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusFortressTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController() //screen moving
    val viewModel: PermissionViewModel = viewModel()

    // observers
    val hasUsageAccessPermission by viewModel.hasUsageAccessPermission.collectAsState() //short for value
    val hasOverlayPermission by viewModel.hasOverlayPermission.collectAsState()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()
    val hasAccessibilityPermission by viewModel.hasAccessibilityPermission.collectAsState()

    val allPermissionsGranted = hasUsageAccessPermission && hasOverlayPermission && hasNotificationPermission && hasAccessibilityPermission

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

    LaunchedEffect(allPermissionsGranted) { //watches allPermissionsGranted and acts if it changes
        if (!allPermissionsGranted && navController.currentDestination?.route != "permissions") {
            navController.navigate("permissions") {
                popUpTo("main") { inclusive = true }
            }
        }
    }

    NavHost(navController, startDestination = if (allPermissionsGranted) "main" else "permissions") {
        composable("permissions") {
            PermissionScreen(
                viewModel = viewModel,
                onPermissionsGranted = {
                    navController.navigate("main") {
                        // Remove permissions screen from back stack
                        popUpTo("permissions") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            MainContent(navController)
        }
        composable("screenTime") {
            ScreenTimeScreen(navController)
        }
        composable("launchCount") {
            LaunchCountScreen(navController)
        }
        composable("blockApp") {
            BlockAppScreen(navController = navController)
        }
        composable("blockWebsite") {
            BlockWebsiteScreen(navController)
        }
        composable("blockKeyword") {
            BlockKeywordScreen(navController)
        }
        composable("takeABreak") {
            TakeABreakScreen(navController)
        }
        composable("selectStrictness") {
            SelectStrictnessScreen(navController)
        }
    }
}
