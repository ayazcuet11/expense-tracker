## 1. Theme-aware palette

- [x] 1.1 Add `AppColors` data class + `LightAppColors` instance + `LocalAppColors` (`staticCompositionLocalOf`) in `ui/theme/`
- [x] 1.2 Design and add `DarkAppColors` (inverted-lightness warm palette per design.md Decision 2)
- [x] 1.3 Rewrite `Color.kt` top-level `val`s as `@Composable get()` accessors delegating to `LocalAppColors.current`
- [x] 1.4 Make `LoanStatus.style()` in `LoanStatusUi.kt` `@Composable` (only non-composable consumer)
- [x] 1.5 Rework `Theme.kt`: `ExpenseTrackerTheme(dark: Boolean, content)` provides `LocalAppColors`, builds the M3 color scheme from the active palette, and sets status-bar color + `isAppearanceLightStatusBars = !dark`
- [x] 1.6 Build check: `./gradlew :app:assembleDebug` (all screens must compile unchanged)

## 2. Persistence and state

- [x] 2.1 Add `androidx.datastore:datastore-preferences` to `libs.versions.toml` and `app/build.gradle.kts`
- [x] 2.2 Create `data/prefs/ThemePreferenceStore.kt`: `ThemePreference` enum (LIGHT, DARK), `theme: Flow<ThemePreference>`, `suspend setTheme(...)`; construct it in `ExpenseApp` alongside the repository
- [x] 2.3 Create `ThemeViewModel` (StateFlow of `ThemePreference`, initial LIGHT, `setTheme`) and register it in `AppViewModelProvider.Factory`
- [x] 2.4 Collect the theme in `MainActivity` and pass `dark` into `ExpenseTrackerTheme`

## 3. Profile drawer

- [x] 3.1 Create `ui/components/ProfileDrawer.kt`: full-screen overlay with fading click-to-dismiss scrim, ~300dp panel sliding in from the right (`slideInHorizontally { it }`), `BackHandler` to close
- [x] 3.2 Drawer content: display name ("Ayaz") header and "Color theme" section with Light/Dark preview cards (mini mock rendered from each theme's own `AppColors`, check badge on the active card, tap → `setTheme`)
- [x] 3.3 Host the drawer in `ExpenseTrackerApp` above the `Scaffold` with `rememberSaveable` open state; thread `onAvatarClick` into `HomeScreen`
- [x] 3.4 Make the Home avatar tappable and derive its initial from the display-name constant

## 4. Verify

- [x] 4.1 Build with `./gradlew :app:assembleDebug` and run on device/emulator
- [x] 4.2 Verify: avatar opens right-side drawer; scrim/back closes it; checkmark tracks the active theme
- [x] 4.3 Verify Dark restyles every surface: all five tabs, Loans + Add screens, RangeSheet/RecordPaymentSheet bottom sheets, dialogs, bottom bar, status bar
- [x] 4.4 Verify persistence: select Dark, force-stop, relaunch → app starts dark
