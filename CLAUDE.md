# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A native Android expense/income tracker: Jetpack Compose + Material 3 UI, Room for persistence,
single-Activity Navigation-Compose. Offline-only — no backend or network. Requires **JDK 17**;
the Android SDK location comes from `sdk.dir` in `local.properties`.

## Commands

```bash
./gradlew :app:assembleDebug      # build debug APK
./gradlew :app:installDebug       # install on connected device / running emulator
./gradlew :app:lint               # Android Lint
./gradlew test                    # JVM unit tests (none exist yet; only the junit dep is wired)
./gradlew :app:connectedDebugAndroidTest   # instrumented tests (none exist yet)
```

There are currently no test sources — `src/test` and `src/androidTest` are absent despite the
junit/AndroidJUnitRunner setup. Build verification is `assembleDebug`.

## Architecture

**Dependency wiring is fully manual — there is no DI framework.**
- `ExpenseApp` (the `Application`) lazily builds the single `ExpenseRepository`, and seeds
  `SampleData` once on first launch when the DB is empty (`seedIfEmpty`).
- `ui/AppViewModelProvider.Factory` constructs every ViewModel with that shared repository.
  **Any new ViewModel must be registered here**, or `viewModel(factory = ...)` will fail at runtime.
- `ExpenseRepository` is the only thing that touches the Room DAO; ViewModels never call Room
  directly.

**Data flow is one-directional and reactive.** The DAO exposes `observeAll(): Flow<List<Expense>>`
ordered by date desc. Each screen's ViewModel maps that single flow into an immutable UI-state
data class (usually via `stateIn`). Writes (add/update/delete) go back through the repository; Room
re-emits the flow and Compose recomposes. There is no per-screen querying — every screen derives
its view from the full transaction list in memory.

**Navigation** lives in `ui/navigation/`. `ExpenseTrackerApp` (in `ui/navigation/ExpenseApp.kt`,
not the `Application` class) hosts the bottom-bar `Scaffold` + `NavHost`. `Routes` (in
`Destinations.kt`) defines route strings; `Routes.mainRoutes` is the set that shows the bottom bar
— the Add screen is a full-screen overlay route (`add?id={id}`) that hides it. Tab switches use
`popUpTo(startDestination){saveState=true}` + `restoreState`. The Add route's optional `id` arg is
a nullable **String** nav arg; `AddTransactionViewModel` reads it from `SavedStateHandle` and
`.toLongOrNull()`s it — non-null means edit mode, null means create.

### Domain model

- The Room entity is `Expense` but the table is named `"transactions"` and represents **both
  income and expenses** — `TransactionType` distinguishes them. `amount` is always stored positive;
  use `Expense.signedAmount` for signed math.
- `Category` is a fixed enum carrying its own `color`, two-letter `mono` badge, `short` label, and
  default `TransactionType`. Categories and `TransactionType` are persisted via `Converters` (stored
  by enum name). **Renaming an enum constant breaks existing rows** — see migration note below.
- Shared analytics live in `ui/screens/common/`: `buildRangeReport(all, range, now)` computes
  totals, per-category breakdown, and month-over-month rising/falling movements; `DateRange.resolve`
  turns a preset (this month / last month / last 3 months) into concrete time windows. The Stats,
  Insights, and Activity ViewModels all build their state from these helpers — change spending math
  here, not in individual screens.

### Room migrations

`ExpenseDatabase` is at `version = 2` with `fallbackToDestructiveMigration()` and
`exportSchema = false`. **Any schema or enum-name change wipes the local DB** (acceptable because
all data is re-seedable demo data). If you ever need to preserve data, replace the destructive
fallback with a real `Migration`.

## Adding a feature screen

1. Add the screen + ViewModel under `ui/screens/<name>/`.
2. Register the ViewModel in `ui/AppViewModelProvider.Factory`.
3. Add a route constant in `Routes` and a `composable(...)` in `ExpenseTrackerApp`; if it's a
   bottom-nav tab, add it to `Routes.mainRoutes` and wire it into `BottomBar`.
4. Derive UI state from `repository.transactions` (and the `common/` helpers for spending math)
   rather than adding new DAO queries unless you genuinely need DB-side filtering.

## Theming

The entire look (indigo primary, mint/coral accents, rounded cards) lives in `ui/theme/`
(`Color.kt`, `Type.kt`, `Theme.kt`), isolated from the screens so re-skinning doesn't touch screen
code. The intended reference design is a Claude-generated `Expense Tracker.dc.html` (not committed).
