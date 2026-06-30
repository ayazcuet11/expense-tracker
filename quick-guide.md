# Expense Tracker — Quick Guide for Android Newcomers

The trick to understanding any Android app is to **follow it in the order the system runs it** —
from launch, down to the database, then back up to the screen. Every step below maps to a real
file in this repo.

## The big picture first

This app has three "layers" stacked on top of each other:

```
UI (Compose screens)  ←  what you see
   ↕
ViewModels (state)    ←  the brain of each screen
   ↕
Data (Room database)  ←  where transactions are stored
```

Data flows **down** (database → screen) and actions flow **back up** (button tap → database).
Keep this picture in mind as you read.

---

## Step 1 — The manifest: the app's "table of contents"

📄 `app/src/main/AndroidManifest.xml`

Every Android app starts here. It's not code — it's a declaration of what the app *is*. The two
lines that matter for you:

- `android:name=".ExpenseApp"` → the **Application** class (runs first, lives the whole time the app is open)
- `<activity android:name=".MainActivity">` with the `LAUNCHER` intent-filter → this is the **screen that opens when you tap the app icon**.

> **Android concept — Activity:** a single screen / entry point into the app. Older apps had many
> Activities; modern Compose apps (like this one) usually have just *one* and swap content inside it.

---

## Step 2 — The Application class: runs once at startup

📄 `ExpenseApp.kt`

This runs before any screen appears. Here it does two jobs:
1. Builds the **repository** (the single object that talks to the database).
2. Calls `seedIfEmpty()` to load demo data on the very first launch.

> **Android concept — Application:** a global object that exists for the app's entire lifetime.
> Good place to set up things every screen shares — here, the database/repository.

---

## Step 3 — MainActivity: the one and only screen host

📄 `MainActivity.kt` (only 21 lines — read the whole thing)

```kotlin
setContent {
    ExpenseTrackerTheme {     // applies colors/fonts
        ExpenseTrackerApp()   // draws the whole UI
    }
}
```

`setContent { }` is the bridge from the old Android world into **Jetpack Compose**. Everything
visual happens inside that block.

> **Android concept — Jetpack Compose:** the modern way to build UI. Instead of XML layout files,
> you write UI as Kotlin functions marked `@Composable`. You *describe* what the screen should look
> like for a given state, and Compose redraws automatically when the state changes.

---

## Step 4 — Navigation: how screens switch

📄 `ui/navigation/ExpenseApp.kt` (the `ExpenseTrackerApp` function)
📄 `ui/navigation/Destinations.kt` (the list of `Routes`)

⚠️ Beginner gotcha: there are **two** things named "ExpenseApp." `ExpenseApp.kt` in the root is the
*Application* (Step 2); `ui/navigation/ExpenseApp.kt` contains the `ExpenseTrackerApp` *composable*
(the UI). Different things.

`ExpenseTrackerApp()` sets up:
- a **bottom navigation bar** (Home / Activity / Stats / Insights)
- a `NavHost` — a map of `"route name" → which screen to show`

Read `Destinations.kt` alongside it: `Routes` is just the string names of each screen.

> **Android concept — Navigation:** since there's only one Activity, "going to another screen" means
> swapping which composable the `NavHost` displays. No new Activity is created.

---

## Step 5 — The data layer (the foundation — read bottom-up)

This is the most important part to understand. Read these four files in this order:

1. 📄 `data/model/Expense.kt` — the **shape of one record**. One row = one transaction. (Note: the class is called `Expense` but it stores both income *and* expense.)
2. 📄 `data/local/ExpenseDao.kt` — the **queries**. `observeAll()` returns a `Flow` — a live stream that re-emits automatically whenever the data changes.
3. 📄 `data/local/ExpenseDatabase.kt` — the **database setup** itself.
4. 📄 `data/repository/ExpenseRepository.kt` — the **single gateway**. Screens never touch the database directly; they go through this.

> **Android concept — Room:** a library that wraps the phone's built-in SQLite database. You define
> an `@Entity` (a table), a `@Dao` (the queries), and a `@Database`, and Room generates all the SQL
> plumbing for you.
>
> **Concept — Flow:** a stream of values over time. Because `observeAll()` is a `Flow`, the UI
> updates *by itself* when you add or delete a transaction — no manual refresh.

---

## Step 6 — ViewModels: the brain of each screen

📄 `ui/screens/add/AddTransactionViewModel.kt` (start here — it's the clearest)
📄 `ui/AppViewModelProvider.kt` (how every ViewModel gets its repository)

A ViewModel sits between the database and the screen. It:
- reads the repository's `Flow`,
- shapes it into a simple `UiState` object the screen can draw,
- and exposes functions (like `save()`) the screen calls when the user acts.

> **Android concept — ViewModel:** holds a screen's state and survives configuration changes (e.g.
> rotating the phone). It does the *thinking*; the screen only *displays*.
>
> ⚠️ Gotcha: every ViewModel must be registered in `AppViewModelProvider.kt`, or the app crashes
> when that screen opens.

---

## Step 7 — A screen: where it all comes together

📄 `ui/screens/home/HomeScreen.kt`

Now you can read a full screen and see the pattern click into place. The key line near the top:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

This says: *"give me the ViewModel's current state, and redraw me whenever it changes."* The rest of
the file just describes boxes, text, and lists using that state.

Then look at the reusable building blocks it uses:
📄 `ui/components/` — `TransactionItem`, `BalanceCard`, `CategoryDonut`, etc. These are small
`@Composable` functions shared across screens.

---

## Suggested reading order (your checklist)

| # | File | What you learn |
|---|------|----------------|
| 1 | `AndroidManifest.xml` | What launches first |
| 2 | `ExpenseApp.kt` | App startup |
| 3 | `MainActivity.kt` | Compose entry point |
| 4 | `ui/navigation/` (both files) | How screens switch |
| 5 | `data/model/Expense.kt` | Data shape |
| 6 | `data/local/ExpenseDao.kt` + `ExpenseDatabase.kt` | Database |
| 7 | `data/repository/ExpenseRepository.kt` | Single data gateway |
| 8 | `ui/screens/add/AddTransactionViewModel.kt` | State logic |
| 9 | `ui/screens/home/HomeScreen.kt` | UI rendering |
| 10 | `ui/components/` + `ui/theme/` | Reusable pieces & styling |

---

## The one exercise that makes it click

Pick one action — **adding a transaction** — and trace it end to end:

```
Tap "+"  →  AddTransactionScreen  →  AddTransactionViewModel.save()
         →  ExpenseRepository.add()  →  ExpenseDao.insert()  →  Room
         →  the Flow re-emits  →  HomeViewModel rebuilds its state
         →  HomeScreen redraws with the new row
```

That single trace touches every layer. Once you can follow it without getting lost, you understand
the whole app.
