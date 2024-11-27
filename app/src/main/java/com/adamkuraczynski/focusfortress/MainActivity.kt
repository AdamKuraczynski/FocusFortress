package com.adamkuraczynski.focusfortress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adamkuraczynski.focusfortress.screens.MainContent
import com.adamkuraczynski.focusfortress.permissions.PermissionScreen
import com.adamkuraczynski.focusfortress.permissions.PermissionViewModel
import com.adamkuraczynski.focusfortress.blocking.BlockKeywordScreen
import com.adamkuraczynski.focusfortress.blocking.BlockWebsiteScreen
import com.adamkuraczynski.focusfortress.strictness.SelectStrictnessScreen
import com.adamkuraczynski.focusfortress.screens.ScheduleScreen
import com.adamkuraczynski.focusfortress.statistics.LaunchCountScreen
import com.adamkuraczynski.focusfortress.statistics.ScreenTimeScreen
import com.adamkuraczynski.focusfortress.ui.theme.FocusFortressTheme
import com.adamkuraczynski.focusfortress.blocking.BlockAppScreen
import com.adamkuraczynski.focusfortress.strictness.PasscodeEntryScreen
import com.adamkuraczynski.focusfortress.strictness.PasscodeSetupScreen
import com.adamkuraczynski.focusfortress.strictness.PasscodeViewModel
import com.adamkuraczynski.focusfortress.strictness.StrictnessViewModel

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
 * @version 1.12
 *
 **/

class MainActivity : ComponentActivity() {
    private val strictnessViewModel: StrictnessViewModel by viewModels()
    private val passcodeViewModel: PasscodeViewModel by viewModels()
    private val permissionViewModel: PermissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusFortressTheme {
                MainApp(
                    strictnessViewModel = strictnessViewModel,
                    passcodeViewModel = passcodeViewModel,
                    permissionViewModel = permissionViewModel
                )
            }
        }
    }
}

@Composable
fun MainApp(
    strictnessViewModel: StrictnessViewModel,
    passcodeViewModel: PasscodeViewModel,
    permissionViewModel: PermissionViewModel
) {
    val navController = rememberNavController() //screen moving

    // observers
    //val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()
    val hasUsageAccessPermission by permissionViewModel.hasUsageAccessPermission.collectAsState() //short for value
    val hasOverlayPermission by permissionViewModel.hasOverlayPermission.collectAsState()
    val hasAccessibilityPermission by permissionViewModel.hasAccessibilityPermission.collectAsState()
    val allPermissionsGranted = hasUsageAccessPermission && hasOverlayPermission && hasAccessibilityPermission //&& hasNotificationPermission

    val strictnessLevel by strictnessViewModel.strictnessLevel.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionViewModel.updatePermissionsStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(allPermissionsGranted) { // watches allPermissionsGranted and acts if it changes
        if (!allPermissionsGranted && navController.currentDestination?.route != "permissions") {
            navController.navigate("permissions") {
                popUpTo("main") { inclusive = true }
            }
        }
    }

    val startDestination = remember {
        if (!allPermissionsGranted) {
            "permissions"
        } else {
            if (strictnessLevel == "Protected") {
                "passcodeEntry"
            } else {
                "main"
            }
        }
    }

    NavHost(navController, startDestination = startDestination) {
        composable("permissions") {
            PermissionScreen(
                viewModel = permissionViewModel,
                onPermissionsGranted = {
                    if (strictnessViewModel.strictnessLevel.value == "Protected") {
                        navController.navigate("passcodeEntry") {
                            popUpTo("permissions") { inclusive = true }
                        }
                    } else {
                        navController.navigate("main") {
                            popUpTo("permissions") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("passcodeEntry") {
            PasscodeEntryScreen(navController, passcodeViewModel)
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
            BlockWebsiteScreen(navController = navController)
        }
        composable("blockKeyword") {
            BlockKeywordScreen(navController = navController)
        }
        composable("selectStrictness") {
            SelectStrictnessScreen(navController, strictnessViewModel)
        }
        composable("passcodeSetup") {
            PasscodeSetupScreen(navController, passcodeViewModel, strictnessViewModel)
        }
        composable("schedules") {
            ScheduleScreen(navController)
        }
    }
}