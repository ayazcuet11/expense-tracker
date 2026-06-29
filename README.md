# Expense Tracker

A native Android expense tracker built with **Jetpack Compose**, **Room**, and a single-Activity
navigation architecture. Track income and expenses, browse them grouped by day, and see a
category breakdown with a donut chart.

## Features

- **Home / Dashboard** — gradient balance card with income & expense summary, plus a list of
  transactions grouped under `Today` / `Yesterday` / dated headers.
- **Add / Edit transaction** — income vs. expense toggle, amount, title, category picker (each
  category has its own icon + color), and an optional note. Tapping an existing row opens it for
  editing.
- **Statistics** — donut chart of spending by category with a ranked breakdown list.
- **Offline-first** — all data persists locally via Room; no backend or network required.

## Tech stack

| Concern        | Choice                                              |
|----------------|-----------------------------------------------------|
| UI             | Jetpack Compose + Material 3                         |
| Navigation     | Navigation-Compose (single Activity)                |
| Persistence    | Room (SQLite) with KSP                               |
| State          | `ViewModel` + `StateFlow` (`collectAsStateWithLifecycle`) |
| DI             | Manual — repository held by the `Application`        |
| Min / Target SDK | 24 / 36                                            |

## Architecture

```
data/
  model/        Expense entity, Category & TransactionType enums
  local/        Room database, DAO, type converters
  repository/   ExpenseRepository (sole gateway to the DAO)
ui/
  theme/        Color / Type / Theme
  navigation/   Routes, bottom tabs, root Scaffold + NavHost (ExpenseTrackerApp)
  components/    BalanceCard, TransactionItem (reused across screens)
  screens/
    home/       HomeScreen + HomeViewModel
    add/        AddTransactionScreen + AddTransactionViewModel
    stats/      StatsScreen + StatsViewModel
  AppViewModelProvider  — wires ViewModels to the shared repository
util/           Currency / date formatters
```

Each screen owns a `ViewModel` that reads from the repository's `Flow<List<Expense>>` and maps it
into an immutable UI-state data class. Writes (add / update / delete) go back through the
repository; the `Flow` re-emits and the UI recomposes automatically.

## Build & run

The project targets the Android SDK installed at the path in `local.properties`
(`sdk.dir`). Requires JDK 17.

```bash
# Build a debug APK
./gradlew :app:assembleDebug

# Install on a connected device / running emulator
./gradlew :app:installDebug
```

Or open the folder in **Android Studio** and run the `app` configuration.

## Design

The intended visuals come from a Claude design (`Expense Tracker.dc.html`). The current theme
(indigo primary, mint/coral accents, rounded cards) is a close approximation. Drop the design's
HTML into this folder to have the colors, spacing, and screen layouts aligned exactly — the theme
lives entirely in `ui/theme/` so re-skinning is isolated from the screens.
