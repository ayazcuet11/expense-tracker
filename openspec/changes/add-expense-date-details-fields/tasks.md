# Tasks: Add Date and Details Fields to the Add Expense Screen

## 1. ViewModel / state

- [x] 1.1 Add `note: String = ""` to `AddUiState`; pre-fill it (alongside the existing `date`) from the loaded `Expense` in edit-mode init
- [x] 1.2 Add `onDateChange(utcMidnightMillis: Long)` to `AddTransactionViewModel` that converts the picker's UTC-midnight millis to a local-zone timestamp on the picked day (per design decision 3) and updates `uiState.date`
- [x] 1.3 Add `onNoteChange(text: String)` updating `uiState.note`
- [x] 1.4 Pass `note = state.note` when building the `Expense` in `save()`

## 2. Screen UI

- [x] 2.1 Add a date field pill to `AddTransactionScreen` between the amount/category header and the category grid, showing the selected date formatted (e.g. "4 Jul 2026") in the existing `PillBg`/`PillBorder` style; add/reuse a date formatter in `util/Formatters.kt`
- [x] 2.2 Wire the pill to a Material 3 `DatePickerDialog` (`rememberDatePickerState` seeded from `uiState.date`); confirm calls `viewModel.onDateChange`, dismiss leaves state untouched
- [x] 2.3 Add a single-line details text field bound to `uiState.note` with placeholder text and `imeAction = Done`, styled to match the pill idiom
- [ ] 2.4 Verify the layout on a small-screen emulator: category grid still scrolls, keypad unobstructed, IME over keypad acceptable

## 3. Verify

- [x] 3.1 `./gradlew :app:assembleDebug` passes
- [ ] 3.2 Manual check: new expense defaults to today; back-date an expense 3 days and confirm it groups under that day in Activity; edit an existing expense and confirm date + details round-trip
