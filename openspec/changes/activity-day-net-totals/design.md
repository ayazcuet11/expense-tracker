# Design: activity-day-net-totals

## Context

The Activity screen (`ui/screens/activity/`) already groups transactions by date via `formatDayLabel` and renders a `DayGroup.dayTotal` on the right of each header. Two defects make the total useless: `ActivityViewModel` computes it as `items.sumOf { it.amount }` (raw positives — income and expense lumped together), and `ActivityScreen` renders it in `InkFaint`, the tertiary caption color. Separately, the shared `TransactionItem` row component hardcodes `formatExpense(expense.amount)`, so income rows show a minus sign.

The domain model already supports signed math: `Expense.signedAmount` (`data/model/Expense.kt:23`) returns positive for `INCOME`, negative for `EXPENSE`. The theme already has `PositiveText` green (`ui/theme/Color.kt:26`).

## Goals / Non-Goals

**Goals:**
- Each Activity date header shows the day's net total (income − expenses), signed and readable.
- Transaction rows render amounts type-aware: `+৳X` in green for income, `-৳X` in ink for expenses — consistently across Activity and Home.

**Non-Goals:**
- No changes to range filtering, grouping keys, or sort order in the Activity timeline.
- No DB/schema, DAO, or repository changes; no changes to `buildRangeReport` or other `ui/screens/common/` analytics.
- No redesign of the header layout — same Row, same typography style.

## Decisions

1. **Net via `signedAmount`, computed in the ViewModel.** Change `dayTotal = items.sumOf { it.amount }` → `items.sumOf { it.signedAmount }` in `ActivityViewModel`. Alternative — computing net in the composable — rejected: state math belongs in the ViewModel per the project's unidirectional-flow pattern, and `DayGroup.dayTotal` has no other consumers, so redefining it as "net" is safe.

2. **New `formatSignedMoney` in `util/Formatters.kt`.** `"+৳1,200"` for positive, `"-৳540"` for negative, `"৳0"` for zero (no sign on zero). Built on the existing `formatMoney`/`groupedFormat`. Alternative — reusing `formatExpense` plus ad-hoc `"+"` prefixes at call sites — rejected: the sign logic would be duplicated and zero handling inconsistent.

3. **Header color `InkFaint` → `InkMuted`.** Matches the day label on the left, making the pair read as one header line. Alternative — color-coding the header green/red by sign — rejected as visually noisy for a timeline header; the sign character carries the meaning. (The row-level amounts do get the green income accent, where individual emphasis is expected.)

4. **Type-aware rows inside `TransactionItem` itself.** Branch on `expense.type`: `INCOME` → `"+" + formatMoney(amount)` in `PositiveText`; `EXPENSE` → `formatExpense(amount)` in `Ink` (unchanged). Because `TransactionItem` is shared, `HomeScreen`'s recent-transactions list is fixed automatically — desired, and keeps a single source of truth. Alternative — a display-variant parameter — rejected: both call sites want identical behavior.

## Risks / Trade-offs

- [Net totals change the meaning of an existing on-screen number] → Acceptable: the old number (income + expense as positives) was incorrect; sign prefix makes the new semantics self-explanatory.
- [`formatSignedMoney` rounds via `roundToLong` before sign check is applied to the formatted value] → Compute sign from the rounded value (or format `abs` and prefix by the raw sign, treating rounded-zero as unsigned) so `-0.4` doesn't render as `-৳0`.
- [Home screen rows change appearance without being named in the request] → Intentional and confirmed with the user ("applies everywhere TransactionItem is used").

## Migration Plan

Pure UI/view-model change; no data migration. Rollback = revert the commit.

## Open Questions

None.
