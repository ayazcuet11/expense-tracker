package com.example.expensetracker.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The full semantic palette the screens draw from. Screens keep using the top-level accessors in
 * Color.kt; those now resolve against the active [AppColors] so switching themes restyles
 * everything without touching screen code.
 */
data class AppColors(
    val accent: Color,
    val accentDark: Color,
    val accentSurface: Color,
    val ink: Color,
    val inkSoft: Color,
    val inkMuted: Color,
    val inkFaint: Color,
    val cream: Color,
    val surface: Color,
    val outline: Color,
    val divider: Color,
    val track: Color,
    val pillBg: Color,
    val pillBorder: Color,
    val dangerText: Color,
    val dangerSurface: Color,
    val positiveText: Color,
    val positiveSurface: Color,
    val amber: Color,
    val amberSurface: Color,
    val onInk: Color,
    val onInkMuted: Color
)

/** Warm, earthy light palette from the design: cream surfaces, ink text, muted green accent. */
val LightAppColors = AppColors(
    accent = Color(0xFF2F7A57),
    accentDark = Color(0xFF276146),
    accentSurface = Color(0xFFE8F2EC),
    ink = Color(0xFF1B1A17),
    inkSoft = Color(0xFF3A372F),
    inkMuted = Color(0xFF736E61),
    inkFaint = Color(0xFFA39C8C),
    cream = Color(0xFFFBF9F4),
    surface = Color(0xFFFFFFFF),
    outline = Color(0xFFEFE9DD),
    divider = Color(0xFFF3EEE4),
    track = Color(0xFFF1ECE2),
    pillBg = Color(0xFFF2EDE3),
    pillBorder = Color(0xFFE7E0D2),
    dangerText = Color(0xFFC7503B),
    dangerSurface = Color(0xFFFBEEEA),
    positiveText = Color(0xFF3E8E6A),
    positiveSurface = Color(0xFFE8F2EC),
    amber = Color(0xFFB58A3C),
    amberSurface = Color(0xFFF6EEDD),
    onInk = Color(0xFFFBF9F4),
    onInkMuted = Color(0xFFA8A293)
)

/**
 * Same warm hues with inverted lightness. Ink and onInk swap roles so the dark "ink cards"
 * (which use `background(Ink)`) stay visually distinct from the near-black background.
 */
val DarkAppColors = AppColors(
    accent = Color(0xFF5FAF87),
    accentDark = Color(0xFF7FC4A0),
    accentSurface = Color(0xFF1E2F27),
    ink = Color(0xFFF2EFE7),
    inkSoft = Color(0xFFD8D3C6),
    inkMuted = Color(0xFFA8A293),
    inkFaint = Color(0xFF7A7466),
    cream = Color(0xFF171613),
    surface = Color(0xFF211F1B),
    outline = Color(0xFF2E2B25),
    divider = Color(0xFF2A2823),
    track = Color(0xFF343128),
    pillBg = Color(0xFF2A2823),
    pillBorder = Color(0xFF3A362E),
    dangerText = Color(0xFFE07A66),
    dangerSurface = Color(0xFF33221E),
    positiveText = Color(0xFF6FBF97),
    positiveSurface = Color(0xFF1E2F27),
    amber = Color(0xFFD8B36A),
    amberSurface = Color(0xFF322A1B),
    onInk = Color(0xFF1B1A17),
    onInkMuted = Color(0xFF736E61)
)

/** Active palette; provided by [ExpenseTrackerTheme]. */
val LocalAppColors = staticCompositionLocalOf { LightAppColors }
