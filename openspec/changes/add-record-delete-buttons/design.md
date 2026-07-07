## Context

The data layer is already delete-capable: `ExpenseRepository.delete(expense)` and
`deleteLoan(loan)` exist (backed by `@Delete` DAO methods), and every screen derives its state
reactively from `repository.transactions` / `repository.loans`, so a delete automatically
propagates to all lists and totals. What's missing is UI:

- Transaction rows are rendered by the shared `ui/components/TransactionItem.kt`, used from
  `HomeScreen.kt:161` and `ActivityScreen.kt:81`. The whole row is one `clickable` that opens
  the edit screen.
- Loan cards are `LoanCard(loan: LoanCardUi, onPay)` in `LoansScreen.kt`; `LoansViewModel`
  already models a per-card interaction sheet (`PaySheetState?`) we can mirror for the
  confirmation state.

## Goals / Non-Goals

**Goals:**
- Trash-icon delete on every transaction row and loan card, with its own touch target.
- A confirmation `AlertDialog` naming the exact record (category + amount / person + principal),
  with Cancel and Delete actions.
- Delete goes through the ViewModel → repository; no new DAO queries.

**Non-Goals:**
- Undo/snackbar after delete.
- Swipe-to-delete gestures.
- Bulk delete or deleting from the edit screen.

## Decisions

1. **Extend `TransactionItem` with an optional `onDelete: (() -> Unit)?` parameter** (trailing
   `IconButton` with `Icons.Outlined.Delete`, shown when non-null) rather than duplicating the
   row per screen. The `IconButton` sits outside the row's `clickable` modifier scope — the row
   click stays on the content `Row`, the icon is a sibling touch target, so event conflicts are
   impossible by construction. Both call sites pass `onDelete`, keeping Home and Activity
   consistent.

2. **Confirmation state lives in each ViewModel as a nullable "pending delete" StateFlow**,
   mirroring the existing `paySheet: StateFlow<PaySheetState?>` pattern in `LoansViewModel`:
   - `HomeViewModel` / `ActivityViewModel`: `pendingDelete: StateFlow<Expense?>` with
     `requestDelete(expense)`, `cancelDelete()`, `confirmDelete()`.
   - `LoansViewModel`: `pendingDelete: StateFlow<LoanCardUi?>` (the card already carries id,
     person, principal — no extra lookup needed).
   Alternative considered: local `remember { mutableStateOf(...) }` in the screens. Rejected —
   the ViewModel-held pattern is what this codebase already uses for sheets, survives config
   changes, and keeps the repository call out of the composable.

3. **One shared confirmation composable** `ConfirmDeleteDialog(title, text, onConfirm, onDismiss)`
   in `ui/components/`, wrapping Material 3 `AlertDialog` with the Delete action colored
   `DangerText`. Screens format the message themselves using the existing `formatMoney` util
   (e.g. "Delete Groceries — $24.50?" / "Delete loan with Sarah — $500.00?").

4. **Loan delete icon placement**: in the card header row next to the status chip, as a compact
   `IconButton`. `LoanCard` gains an `onDelete: () -> Unit` parameter alongside `onPay`.

## Risks / Trade-offs

- [Deleting a loan with recorded payments silently discards payment history] → the confirmation
  dialog names person + principal; loans are self-contained rows (payments are a column, not
  child rows), so no orphaned data results.
- [40dp icon buttons tighten row layout on narrow screens] → amount text already ellipsizes;
  use `Modifier.size(40dp)` compact IconButton and keep the amount before the icon.
- [No undo] → accepted; confirmation dialog is the guard, and all data is re-seedable demo data.
