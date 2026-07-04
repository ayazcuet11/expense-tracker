# Tasks: activity-day-net-totals

## 1. Formatting utility

- [x] 1.1 Add `formatSignedMoney(amount: Double)` to `util/Formatters.kt`: round first, then prefix `+`/`-` from the rounded value (`+৳1,200`, `-৳540`), returning unsigned `৳0` when it rounds to zero; reuse the existing `groupedFormat`.

## 2. Net day totals in Activity

- [x] 2.1 In `ui/screens/activity/ActivityViewModel.kt`, change the `DayGroup` construction to `dayTotal = items.sumOf { it.signedAmount }`.
- [x] 2.2 In `ui/screens/activity/ActivityScreen.kt` (header row, ~line 73), render the total with `formatSignedMoney(group.dayTotal)` and change its color from `InkFaint` to `InkMuted`.

## 3. Type-aware transaction rows

- [x] 3.1 In `ui/components/TransactionItem.kt`, branch on `expense.type`: INCOME → `"+" + formatMoney(expense.amount)` in `PositiveText`; EXPENSE → keep `formatExpense(expense.amount)` in `Ink`. Update imports (`TransactionType`, `PositiveText`, `formatMoney`).

## 4. Verification

- [x] 4.1 Build: `./gradlew :app:assembleDebug` (project has no test sources; build is the baseline check).
- [x] 4.2 Manual check via `./gradlew :app:installDebug`: Activity headers show signed net per date (`-৳…` spend day, `+৳…` income day) in the muted color; income rows are green with `+` and expense rows unchanged, on both Activity and Home screens.
