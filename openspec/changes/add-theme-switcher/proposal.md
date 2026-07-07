## Why

The app ships a single hard-coded warm-light theme and the profile avatar in the Home header is
decorative. Users have no way to switch to a dark look, and no profile surface exists to hang
such settings on.

## What Changes

- Make the profile avatar (the "A" circle, top-right of the Home header) tappable.
- Tapping it opens a profile panel that slides in from the **right** edge as a sidebar drawer,
  overlaying the whole app, dismissible via scrim tap.
- The panel shows the user's display name and a "Color theme" section with two themes —
  **Light** (current warm palette) and **Dark** — each rendered as a small preview card with a
  checkmark on the active one.
- Selecting a theme restyles the entire app immediately: every screen, bottom bar, sheet, and
  dialog. This requires making the currently static color palette (`ui/theme/Color.kt` `val`s
  used directly by every screen) theme-aware.
- The chosen theme persists across app restarts (DataStore Preferences).

## Capabilities

### New Capabilities
- `theme-switching`: Selecting and persisting an app-wide color theme from a profile drawer.

### Modified Capabilities

(none — no existing specs)

## Impact

- **New dependency**: `androidx.datastore:datastore-preferences` for persistence.
- `ui/theme/Color.kt` — becomes an `AppColors` palette (light + dark instances) exposed through a
  `CompositionLocal`; existing top-level color `val`s become `@Composable` getters so ~all call
  sites compile unchanged.
- `ui/theme/Theme.kt` — builds the Material color scheme from the active palette, toggles status
  bar appearance per theme.
- `ui/screens/loans/LoanStatusUi.kt` — `LoanStatus.style()` becomes `@Composable` (only
  non-composable consumer of theme colors).
- `ExpenseApp` (Application) or `MainActivity` — provides the theme preference store; new
  `data/prefs/` module + a small ViewModel/state holder for the current theme.
- `ui/navigation/ExpenseApp.kt` + `HomeScreen.kt` — avatar click callback; new profile drawer
  composable overlaying the scaffold.
