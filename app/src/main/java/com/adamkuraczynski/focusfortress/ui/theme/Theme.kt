package com.adamkuraczynski.focusfortress.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.adamkuraczynski.focusfortress.R

/**
 * Theme configuration for the FocusFortress application.
 *
 * Sets up color schemes and typography, supporting both light and dark themes,
 * as well as dynamic color adaptation on supported devices.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.3
 *
 **/

/**
 * The custom font family used in the app, based on the "Old London" font.
 */
val MedievalFont = FontFamily(
    Font(R.font.oldlondon, weight = FontWeight.Normal)
)

/**
 * Custom typography styles used throughout the app.
 */
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = MedievalFont,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal
    ),
    titleLarge = TextStyle(
        fontFamily = MedievalFont,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontFamily = MedievalFont,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium
    )
)

/**
 * Color scheme used in dark theme mode.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * Color scheme used in light theme mode.
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * Applies the FocusFortress theme to the composables.
 *
 * @param darkTheme Determines if dark theme should be used. Defaults to system setting.
 * @param dynamicColor Enables dynamic color theming on Android 12+ devices.
 * @param content The composable content to apply the theme to.
 *
 */
@Composable
fun FocusFortressTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}