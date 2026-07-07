package com.example.expensetracker.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Theme-aware accessors: each resolves against the active AppColors palette, so every existing
// call site restyles automatically when the theme switches. They only work inside composition —
// a non-composable consumer must take the colors as parameters instead (see LoanStatusUi).

val Accent: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.accent
val AccentDark: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.accentDark
val AccentSurface: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.accentSurface

// Neutrals
val Ink: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.ink            // primary text + dark cards
val InkSoft: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.inkSoft
val InkMuted: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.inkMuted  // secondary text
val InkFaint: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.inkFaint  // tertiary / captions
val Cream: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.cream        // screen background
val Surface: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.surface    // cards
val Outline: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.outline    // card borders
val Divider: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.divider    // row separators
val Track: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.track        // progress-bar tracks
val PillBg: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.pillBg      // pill / chip background
val PillBorder: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.pillBorder

// Semantic deltas
val DangerText: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.dangerText
val DangerSurface: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.dangerSurface
val PositiveText: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.positiveText
val PositiveSurface: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.positiveSurface
val Amber: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.amber
val AmberSurface: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.amberSurface

// Text on ink cards
val OnInk: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.onInk
val OnInkMuted: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.onInkMuted
