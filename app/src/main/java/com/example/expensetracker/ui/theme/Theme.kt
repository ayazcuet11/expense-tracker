package com.example.expensetracker.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// The design is a single warm light theme; we mirror it rather than offering a dark variant.
private val LightColors = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    primaryContainer = AccentSurface,
    onPrimaryContainer = AccentDark,
    secondary = Ink,
    onSecondary = OnInk,
    secondaryContainer = PillBg,
    onSecondaryContainer = Ink,
    error = DangerText,
    onError = Color.White,
    errorContainer = DangerSurface,
    onErrorContainer = DangerText,
    background = Cream,
    onBackground = Ink,
    surface = Surface,
    onSurface = Ink,
    surfaceVariant = PillBg,
    onSurfaceVariant = InkMuted,
    outline = Outline,
    outlineVariant = Divider
)

@Composable
fun ExpenseTrackerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
