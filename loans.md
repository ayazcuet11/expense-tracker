# Loans Feature — Implementation Plan

## Context

The Claude Design project `Expense Tracker.dc.html` (read via the `claude_design` MCP /
`DesignSync` tool — read-only; nothing is pushed back) extends the existing tracker with a
**Loans** domain: money the user has **borrowed** (you owe) or **lent** (you're owed), each
paid down over time. The design touches five surfaces:

1. **Loans screen** — "You owe" / "You're owed" net tiles, an `I owe · n / Owed to me · n` tab
   toggle, and per-loan cards (avatar initial, person, note, remaining, progress bar,
   `paid of principal · pct%`, status badge, "Record payment").
2. **Add Loan overlay** — direction toggle (I borrowed / I lent), person, amount, date borrowed,
   due date, note → "Save loan".
3. **Record Payment bottom sheet** — amount input with **Half** / **Pay full** shortcuts.
4. **Home summary card** — You owe / You're owed tiles + overdue note; tap opens Loans.
5. **Insights "Loan reminders"** — overdue / due-soon loans surfaced at the top of Insights.

Loans are a **separate domain from transactions** — recording a payment updates the loan only, it
does not create an `Expense` row. The implementation is native (Compose + Room), mirroring the
existing manual-DI, single-reactive-flow architecture; the HTML is reference only.

## Data model

**`data/model/Loan.kt`** — new Room entity (table `"loans"`), mirroring `Expense` conventions
(autoGen `Long` id, epoch-millis `Long` dates, computed `get()` props):

```kotlin
@Entity(tableName = "loans")
data class Loan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val person: String,
    val direction: LoanDirection,      // BORROWED = "you owe", LENT = "you're owed"
    val principal: Double,             // always positive
    val paidAmount: Double = 0.0,      // accumulator, clamped 0..principal
    val borrowedDate: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val note: String = "",
) {
    val remaining: Double get() = (principal - paidAmount).coerceAtLeast(0.0)
    val progress: Float get() = if (principal <= 0) 0f else (paidAmount / principal).toFloat().coerceIn(0f, 1f)
    val isSettled: Boolean get() = remaining <= 0.0
    fun status(nowMillis: Long): LoanStatus = when {
        isSettled -> LoanStatus.SETTLED
        dueDate != null && dueDate < nowMillis -> LoanStatus.OVERDUE
        dueDate != null && dueDate - nowMillis <= 7L * 24 * 3600 * 1000 -> LoanStatus.DUE_SOON
        else -> LoanStatus.ACTIVE
    }
}
```

**`data/model/LoanDirection.kt`** — `enum class LoanDirection { BORROWED, LENT }`.

**`data/model/LoanStatus.kt`** — `enum class LoanStatus(val label)` for
`ACTIVE / DUE_SOON / OVERDUE / SETTLED`. The `label` doubles as badge text. Badge colors are mapped
in a UI helper (`ui/screens/loans/LoanStatusUi.kt`) using theme tokens: OVERDUE →
`DangerText`/`DangerSurface`, DUE_SOON → `Amber`/`AmberSurface`, ACTIVE → `Accent`/`AccentSurface`,
SETTLED → `InkMuted`/`PillBg`.

A loan card's remaining color = `DangerText` when `direction == BORROWED`, `PositiveText` when
`LENT` (coral vs green per design).

## Persistence wiring

- **`data/local/Converters.kt`** — add a converter pair for `LoanDirection`
  (`fromX(x)=x.name` / `toX(s)=X.valueOf(s)`). `LoanStatus` is computed, not stored.
- **`data/local/LoanDao.kt`** — mirror `ExpenseDao`: `observeAll(): Flow<List<Loan>>`
  (`ORDER BY borrowedDate DESC`), `suspend getById`, `count`, `insert`, `insertAll`, `update`,
  `delete`, `clear`.
- **`data/local/ExpenseDatabase.kt`** — add `Loan::class` to `entities`, add
  `abstract fun loanDao(): LoanDao`, bump `version = 2 → 3`. Destructive fallback already set, so
  no `Migration` needed (demo data re-seeds).
- **`data/repository/ExpenseRepository.kt`** — extend the **existing** repository (preserves the
  CLAUDE.md invariant "ExpenseRepository is the only thing that touches the Room DAO" and avoids
  threading a second repo through the factory). Constructor takes `loanDao: LoanDao` as a second
  param; expose `val loans: Flow<List<Loan>> = loanDao.observeAll()` plus
  `suspend addLoan/updateLoan/deleteLoan/getLoan(id)` and a
  `suspend recordPayment(id, amount)` helper that loads, increments `paidAmount`
  (clamped to principal), and updates. Extend `seedIfEmpty()` to also seed sample loans when
  `loanDao.count() == 0`.
- **`ExpenseApp.kt`** — build the repo with both DAOs:
  `ExpenseRepository(db.expenseDao(), db.loanDao())`.
- **`data/SampleData.kt`** — add `fun seedLoans(now): List<Loan>` returning ~4 demo loans (mix of
  BORROWED/LENT, one overdue, one due-soon, one partially paid) so the screens are populated on
  first launch.

## ViewModels (register every one in `ui/AppViewModelProvider.kt`)

- **`ui/screens/loans/LoansViewModel.kt`** — reactive-read pattern. `combine(repository.loans,
  tabFlow)` → `LoansUiState` (owe/lent totals + counts via reduction over `remaining`, the filtered
  `List<LoanCardUi>` for the active tab with `progress`, `status`, formatted fields). Also owns the
  record-payment sheet: `paySheet: MutableStateFlow<PaySheetState?>`, `openPayment(loanId)`,
  `setPayAmount`, `payHalf()`/`payFull()` (fill from the loan's `remaining`), `savePayment()` →
  `viewModelScope.launch { repository.recordPayment(...) }`. `setTab(LoanDirection)`.
- **`ui/screens/loans/AddLoanViewModel.kt`** — form pattern (mirror `AddTransactionViewModel`):
  self-owned `MutableStateFlow(AddLoanUiState())` with `update {}`; fields direction/person/
  amount/borrowedDate/dueDate/note; computed `canSave`; `save(onDone)` →
  `repository.addLoan(...)`. Add-only (no nav-arg/edit mode needed for the design).
- **`HomeViewModel` / `InsightsViewModel`** — extend their `combine(...)` to also take
  `repository.loans`: Home derives the summary-card state (owe/lent remaining, counts, overdue
  flag); Insights derives `reminders` = loans with status OVERDUE/DUE_SOON mapped to reminder rows
  (only shown when non-empty, per the design's `hasReminders` guard).

## Screens & components

- **`ui/screens/loans/LoansScreen.kt`** — `viewModel(factory = AppViewModelProvider.Factory)`,
  `collectAsStateWithLifecycle()`. Header (title + a back chevron, since Loans is a pushed screen,
  and an "Add loan" dark pill calling `onAddLoan`), two net tiles, the segmented tab toggle, and a
  `LazyColumn` of loan cards built from `SectionCard` + theme tokens. Each card's "Record payment"
  button calls `viewModel.openPayment(loan.id)`. The record-payment sheet
  (`RecordPaymentSheet.kt`) is a `ModalBottomSheet` driven by `paySheet`. Takes nav lambdas
  `onBack`, `onAddLoan`.
- **`ui/screens/loans/AddLoanScreen.kt`** — full-screen overlay matching the Add-expense overlay
  chrome (close button). Direction segmented control, text/number inputs (`৳` currency prefix),
  Material3 `DatePickerDialog` date fields, "Save loan" → `viewModel.save { onBack() }`.
- Reuse `SectionCard`, theme colors, `util/Formatters.formatMoney`/`formatDayLabel`. Loan avatar is
  an initial-in-rounded-square rendered inline.

## Navigation (`ui/navigation/`)

- **`Destinations.kt`** — add `const val LOANS = "loans"` and `const val ADD_LOAN = "addLoan"`.
  **Not** added to `mainRoutes` — Loans is reached from the Home card (and Insights), not a bottom
  tab (the custom `BottomBar` only fits 4 tabs around the center FAB; the design itself links Loans
  from Home, not the nav bar). Both routes stay full-screen (no bottom bar).
- **`ExpenseApp.kt` (`ExpenseTrackerApp`)** — add `composable(Routes.LOANS){ LoansScreen(onBack =
  { navController.popBackStack() }, onAddLoan = { navController.navigate(Routes.ADD_LOAN) }) }` and
  `composable(Routes.ADD_LOAN){ AddLoanScreen(onBack = { navController.popBackStack() }) }`. Wire
  the Home screen's loans-card click and the Insights reminders to
  `navController.navigate(Routes.LOANS)`.
- **`HomeScreen` / `InsightsScreen`** — add the new summary card / reminders section, taking an
  `onOpenLoans: () -> Unit` lambda.

## Verification

1. **Build:** `./gradlew :app:assembleDebug` (no test sources exist; build is the gate). ✅ passes.
2. **Lint:** `./gradlew :app:lint`. ✅ passes.
3. **Run:** `./gradlew :app:installDebug` on an emulator/device. Because the DB version bumps to 3
   with destructive fallback, first launch wipes and re-seeds — confirm sample loans appear.
4. **Manual walkthrough (matches the design):**
   - Home shows the Loans summary card with non-zero You owe / You're owed and the overdue dot;
     tapping it opens the Loans screen.
   - Loans screen: net tiles match the summary; tab toggle switches between borrowed/lent lists;
     each card shows progress, `paid of principal · pct%`, and a status badge (one overdue red, one
     due-soon amber).
   - "Add loan" → fill the form (try both directions, a due date) → Save → the new loan appears in
     the correct tab and tiles update.
   - "Record payment" → sheet opens with remaining; **Half** and **Pay full** prefill correctly;
     Save advances the progress bar and reduces remaining; paying full flips the card to Settled and
     hides "Record payment".
   - Insights shows the "Loan reminders" section listing the overdue/due-soon loans.
