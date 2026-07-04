# Design: Add Date and Details Fields to the Add Expense Screen

## Context

The Add/Edit Expense screen (`ui/screens/add/`) is a full-screen overlay: amount display on top,
a 4-column category grid in the middle, and a keypad + save button pinned to the bottom.
`AddUiState` already carries `date: Long` (defaulted to `System.currentTimeMillis()`, pre-filled
in edit mode) but exposes no way to change it, and the `Expense.note` column exists but is never
read or written by the form. The reference design (`Expense Tracker.dc.html` in the user's Claude
Design project) adds a date field and a details field to this form; the design file could not be
fetched this session (DesignSync requires `/design-login`), so layout follows the existing
screen's pill/card idiom.

## Goals / Non-Goals

**Goals:**
- Let the user pick the transaction date (defaulting to today) via a Material 3 date picker.
- Let the user enter optional free-text details, persisted to the existing `note` column.
- Preserve both values round-trip in edit mode.

**Non-Goals:**
- No Room schema, DAO, or repository changes (`date` and `note` columns already exist).
- No display of the note elsewhere (Activity/Home rows keep showing `title`); that's a separate
  change if wanted.
- No time-of-day picker — date only.

## Decisions

1. **Placement: a compact row of two field pills between the amount/category header and the
   category grid.** The keypad and grid already consume most of the vertical space; two small
   tappable pills (date on the left, details on the right or full-width beneath) match the
   existing `PillBg`/`PillBorder` visual language without crowding the keypad. Alternative —
   putting fields below the grid — was rejected because the grid is `weight(1f)` and fields
   would collide with the keypad on small screens.

2. **Date picker: `DatePickerDialog` + `rememberDatePickerState` from Material 3.** Already in
   the dependency tree (compose-material3); no new dependency. The dialog is state-hoisted in the
   screen with a local `showDatePicker` boolean; confirmation calls `viewModel.onDateChange(millis)`.

3. **Preserve time-of-day when the calendar day changes.** `DatePickerState.selectedDateMillis`
   is UTC-midnight of the picked day. The ViewModel converts it to a local-zone timestamp on the
   picked day (keeping the current state's time-of-day where sensible, or noon local) so that
   day-grouping in `buildRangeReport`/Activity buckets correctly regardless of timezone offset.
   Storing raw UTC midnight was rejected: for UTC+6 (user's likely zone, BDT) it lands at 6 AM
   local same day — fine — but for negative offsets it lands on the *previous* local day.

4. **Details input: a plain `BasicTextField`/`OutlinedTextField`-style field bound to
   `AddUiState.note`.** The keypad only drives the amount; the details field uses the soft
   keyboard as normal Compose text input. `title` handling is untouched — `save()` keeps the
   `"Added expense"` fallback; `note` is saved verbatim (blank allowed).

5. **No new ViewModel or route.** Everything extends `AddUiState`, `AddTransactionViewModel`
   (new `onDateChange`, `onNoteChange`), and `AddTransactionScreen`. Formatting the date label
   reuses `util/Formatters.kt` conventions (e.g. `"d MMM yyyy"`).

## Risks / Trade-offs

- [Soft keyboard over the custom keypad] Focusing the details field raises the IME on top of the
  amount keypad; on small screens the layout may compress. → Keep the details field single-line
  with `imeAction = Done`, and rely on the existing `Column` layout; verify on a small emulator.
- [Design fidelity] The exact visual spec is unavailable this session. → Implementation follows
  the existing pill idiom; run `/design-login` and re-sync `Expense Tracker.dc.html` if pixel
  fidelity matters, then adjust styling only (no logic changes expected).
- [Timezone bucketing] Wrong conversion of the picker's UTC millis would file transactions under
  the wrong day. → Decision 3 pins conversion to the local zone; the spec's back-dating scenario
  covers it.
