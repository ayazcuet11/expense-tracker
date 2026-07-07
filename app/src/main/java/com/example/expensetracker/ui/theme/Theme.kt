package com.example.expensetracker.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun ExpenseTrackerTheme(
    dark: Boolean = false,
    content: @Composable () -> Unit
) {
    val appColors = if (dark) DarkAppColors else LightAppColors
    // Mirror the palette into the M3 scheme so Material components (dialogs, sheets, dividers)
    // follow the active theme without per-usage overrides.
    val colorScheme = if (dark) {
        darkColorScheme(
            primary = appColors.accent,
            onPrimary = Color.White,
            primaryContainer = appColors.accentSurface,
            onPrimaryContainer = appColors.accentDark,
            secondary = appColors.ink,
            onSecondary = appColors.onInk,
            secondaryContainer = appColors.pillBg,
            onSecondaryContainer = appColors.ink,
            error = appColors.dangerText,
            onError = Color.White,
            errorContainer = appColors.dangerSurface,
            onErrorContainer = appColors.dangerText,
            background = appColors.cream,
            onBackground = appColors.ink,
            surface = appColors.surface,
            onSurface = appColors.ink,
            surfaceVariant = appColors.pillBg,
            onSurfaceVariant = appColors.inkMuted,
            outline = appColors.outline,
            outlineVariant = appColors.divider
        )
    } else {
        lightColorScheme(
            primary = appColors.accent,
            onPrimary = Color.White,
            primaryContainer = appColors.accentSurface,
            onPrimaryContainer = appColors.accentDark,
            secondary = appColors.ink,
            onSecondary = appColors.onInk,
            secondaryContainer = appColors.pillBg,
            onSecondaryContainer = appColors.ink,
            error = appColors.dangerText,
            onError = Color.White,
            errorContainer = appColors.dangerSurface,
            onErrorContainer = appColors.dangerText,
            background = appColors.cream,
            onBackground = appColors.ink,
            surface = appColors.surface,
            onSurface = appColors.ink,
            surfaceVariant = appColors.pillBg,
            onSurfaceVariant = appColors.inkMuted,
            outline = appColors.outline,
            outlineVariant = appColors.divider
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !dark
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
