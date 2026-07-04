# Proposal: Add Date and Details Fields to the Add Expense Screen

## Why

The Add Expense screen currently offers only amount, category, and (implicitly) a title — every
transaction is stamped with "now" and saved with an empty note. Users cannot back-date a
transaction they forgot to log, and cannot attach any free-text details. The updated reference
design (`Expense Tracker.dc.html`, Claude Design project) adds two fields to the add form: **date**
and **details**.

## What Changes

- Add a **date field** to the Add/Edit Expense screen: shows the currently selected transaction
  date (defaulting to today for new entries) and opens a Material 3 date picker to change it.
- Add a **details field** to the Add/Edit Expense screen: a free-text input persisted to the
  existing (currently unused) `Expense.note` column.
- Wire both fields through `AddUiState` / `AddTransactionViewModel.save()`, including edit mode:
  editing an existing transaction pre-fills its stored date and note, and saving preserves them.
- No schema change: `Expense.date` and `Expense.note` already exist in the Room entity, so the DB
  version stays at 2 and no data is wiped.

## Capabilities

### New Capabilities

- `add-transaction-form`: Requirements for the date and details inputs on the Add/Edit Expense
  screen — defaults, editing pre-fill, persistence, and display formatting.

### Modified Capabilities

<!-- none — no archived specs exist yet; prior change-local specs (add-amount-display,
     activity-day-totals, transaction-amount-display) are unaffected -->

## Impact

- `ui/screens/add/AddTransactionViewModel.kt` — `AddUiState` gains `note`; new `onDateChange` /
  `onNoteChange` handlers; `save()` passes `note`; edit-mode init pre-fills it.
- `ui/screens/add/AddTransactionScreen.kt` — new date + details row(s) between the amount/category
  area and the keypad, per the reference design; `DatePickerDialog` integration.
- `util/Formatters.kt` — reuse (or add) a date formatter for the field label.
- No changes to Room entity, DAO, repository, navigation, or other screens. Downstream screens
  (Activity/Stats/Insights) already group by `Expense.date`, so back-dated entries flow into the
  correct buckets automatically.
