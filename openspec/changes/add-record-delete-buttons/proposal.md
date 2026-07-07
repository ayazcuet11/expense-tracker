## Why

There is currently no way to remove a record once it exists: expense/income rows and loan cards
can be created and edited, but never deleted. The repository and DAOs already expose
`delete(expense)` and `deleteLoan(loan)` — the capability is simply not wired into any UI.

## What Changes

- Add a trash-icon delete button to every transaction row (`TransactionItem`, used on Home and
  Activity screens).
- Add a trash-icon delete button to every loan card on the Loans screen.
- Tapping delete opens a confirmation dialog that names exactly what will be removed:
  - Transactions: category and amount (e.g. "Delete Groceries — $24.50?").
  - Loans: person and principal (e.g. "Delete loan with Sarah — $500.00?").
- The dialog offers Cancel and Delete actions; only Delete performs the removal.
- The delete button is a separate touch target: tapping it must NOT trigger the row's existing
  click action (which opens the transaction in the edit screen).

## Capabilities

### New Capabilities
- `record-deletion`: Deleting transactions and loans from the UI with per-record confirmation.

### Modified Capabilities

(none — no existing specs)

## Impact

- `ui/components/TransactionItem.kt` — add trailing delete icon + separate click handling.
- `ui/screens/home/HomeScreen.kt`, `ui/screens/activity/ActivityScreen.kt` — pass delete callbacks,
  host the confirmation dialog; `HomeViewModel` / `ActivityViewModel` gain a delete action.
- `ui/screens/loans/LoansScreen.kt` / `LoansViewModel.kt` — delete icon on `LoanCard`,
  confirmation dialog, delete action.
- No data-layer changes: `ExpenseRepository.delete` / `deleteLoan` and the DAO methods already exist.
- No navigation, schema, or dependency changes.
