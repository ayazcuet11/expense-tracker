## Context

`Theme.kt` defines a single `lightColorScheme` and `Color.kt` exposes ~20 top-level color `val`s
(`Ink`, `Cream`, `PillBg`, …) that screens import and use **directly** — most styling bypasses
`MaterialTheme.colorScheme`. The avatar is a static "A" `Box` in `HomeScreen.kt:89`. There is no
preferences storage; the only non-composable consumer of theme colors is
`LoanStatus.style()` in `LoanStatusUi.kt`. `Category` enum colors are hard-coded per category
(identity colors, not theme colors).

## Goals / Non-Goals

**Goals:**
- Light + Dark palettes switchable at runtime from a right-side profile drawer, persisted via
  DataStore Preferences.
- Every existing color call site keeps compiling — the refactor must not touch dozens of screens.
- Status bar and Material color scheme follow the active palette.

**Non-Goals:**
- Following the system dark-mode setting (explicit user choice only, per request).
- More than two themes (structure allows adding more later).
- Editable user profile (name is a constant; avatar initial derives from it).
- Re-tinting `Category` identity colors per theme.

## Decisions

1. **Palette via `CompositionLocal`, existing `val`s become `@Composable` getters.**
   Introduce `data class AppColors(val ink: Color, val cream: Color, …)` with `LightAppColors`
   and `DarkAppColors` instances, provided by `staticCompositionLocalOf` from
   `ExpenseTrackerTheme`. Each existing top-level `val` in `Color.kt` is rewritten as
   `val Ink: Color @Composable get() = LocalAppColors.current.ink`. Since every consumer except
   `LoanStatus.style()` already reads these inside composables, call sites compile unchanged;
   `style()` just gains `@Composable`. Alternative — migrating all screens to
   `MaterialTheme.colorScheme` — was rejected: M3 has no slots for `Track`/`PillBg`/`Amber` etc.,
   and it would touch every screen file for no behavioral gain.

2. **Dark palette values**: keep the same hue relationships (warm neutrals, muted green accent)
   with inverted lightness: near-black warm background (`~0xFF171613`), dark card surface,
   cream-ish ink text, brighter accent/danger/positive text with dark translucent surfaces.
   `OnInk`/`Ink` swap roles for the dark "ink cards" so they stay visually distinct from the
   background. `Theme.kt` builds `lightColorScheme`/`darkColorScheme` from the active `AppColors`
   (background, surface, error, etc. as today) so M3 components (AlertDialog, ModalBottomSheet,
   HorizontalDivider defaults) follow automatically, and sets
   `isAppearanceLightStatusBars = !dark`.

3. **Persistence: DataStore Preferences with a `ThemePreference` enum (`LIGHT`, `DARK`).**
   New `data/prefs/ThemePreferenceStore.kt` exposing `theme: Flow<ThemePreference>` and
   `suspend fun setTheme(...)`, constructed in `ExpenseApp` (the Application) like the repository.
   A new `ThemeViewModel` (registered in `AppViewModelProvider.Factory`) exposes
   `StateFlow<ThemePreference>` + `setTheme`. `MainActivity` collects it and passes
   `dark = theme == DARK` into `ExpenseTrackerTheme`. Initial value: read synchronously via
   `runBlocking { flow.first() }`? No — use `stateIn` with initial `LIGHT`; a one-frame light
   flash on dark-preference cold start is acceptable for this demo app (alternative
   `runBlocking` in `Application.onCreate` rejected as a bad pattern to model).

4. **Drawer: custom overlay, not `ModalNavigationDrawer`.** M3's drawer opens from the start
   (left) edge; the RTL-flip workaround (`CompositionLocalProvider(LocalLayoutDirection)`) is
   fragile and flips content too. Instead, a `ProfileDrawer` composable in `ui/components/`:
   full-screen `Box` with an `AnimatedVisibility` scrim (fade, click-to-dismiss) and a
   `slideInHorizontally { it }` panel (~300dp, full height) aligned `CenterEnd`, plus
   `BackHandler` when open. Hosted in `ExpenseTrackerApp` **above** the `Scaffold` content so it
   overlays every screen and the bottom bar; open-state is a `rememberSaveable` boolean toggled
   by a new `onAvatarClick` callback threaded to `HomeScreen`.

5. **Drawer contents**: display-name constant (`"Ayaz"`, avatar initial derives from its first
   letter — replaces the hard-coded "A") and a "Color theme" section with two preview cards.
   Each card is a mini mock (background swatch + ink text bar + accent dot rendered from that
   theme's `AppColors`, not the active one) with the theme name below and a check icon badge on
   the active card. Cards call `ThemeViewModel.setTheme` on click.

## Risks / Trade-offs

- [Composable-getter colors can't be read outside composition] → acceptable; the codebase's only
  offender is `LoanStatus.style()`, fixed in this change. New non-composable usages will fail to
  compile loudly, which is the desired guardrail.
- [One-frame light flash on cold start with dark preference] → accepted (Decision 3); can later
  be removed with a splash-screen-gated first frame.
- [Hard-coded `Category` colors may have weaker contrast on dark surfaces] → badges keep their
  own colored backgrounds with white monograms, so contrast is unaffected; donut/legend colors
  remain legible on dark. Revisit only if visual QA flags it.
- [Custom drawer re-implements scrim/animation M3 gives for free] → small, isolated composable;
  the M3 drawer's left-edge behavior is a hard mismatch with the required right-side slide.
