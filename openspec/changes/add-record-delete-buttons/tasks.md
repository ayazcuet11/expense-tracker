## 1. Shared components

- [ ] 1.1 Create `ui/components/ConfirmDeleteDialog.kt`: Material 3 `AlertDialog` wrapper taking title, message text, `onConfirm`, `onDismiss`; Delete action styled with `DangerText`, Cancel dismisses
- [ ] 1.2 Add optional `onDelete: (() -> Unit)? = null` to `TransactionItem`: when non-null, render a trailing compact `IconButton` with `Icons.Outlined.Delete` as a sibling of the clickable row content so the row click is not triggered

## 2. Transactions (Home + Activity)

- [ ] 2.1 Add `pendingDelete: StateFlow<Expense?>` with `requestDelete` / `cancelDelete` / `confirmDelete` (calls `repository.delete`) to `ActivityViewModel`
- [ ] 2.2 Wire `ActivityScreen`: pass `onDelete` to `TransactionItem`, and show `ConfirmDeleteDialog` naming category label + formatted amount when `pendingDelete` is non-null
- [ ] 2.3 Add the same `pendingDelete` trio to `HomeViewModel` and wire the Home recent-transactions list + dialog the same way

## 3. Loans

- [ ] 3.1 Add `pendingDelete: StateFlow<LoanCardUi?>` with `requestDelete` / `cancelDelete` / `confirmDelete` (loads the `Loan` via `repository.getLoan` then `repository.deleteLoan`) to `LoansViewModel`
- [ ] 3.2 Add `onDelete: () -> Unit` to `LoanCard` and render a trash `IconButton` in the card header row, separate from the pay action
- [ ] 3.3 Show `ConfirmDeleteDialog` in `LoansScreen` naming person + formatted principal when `pendingDelete` is non-null

## 4. Verify

- [x] 4.1 Build with `./gradlew :app:assembleDebug`
- [x] 4.2 Manually verify on device/emulator: delete icon tap opens the dialog without navigating to edit; Cancel keeps the record; Delete removes it from Home, Activity, Stats totals, and the Loans tiles respectively
