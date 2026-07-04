# Proposal: activity-day-net-totals

## Why

The Activity screen's per-date header total sums raw `amount` values, lumping income and expenses together into a meaningless positive number rendered in a near-invisible faint color — users can't tell what a day actually cost or earned them. Relatedly, every transaction row shows a minus sign even for income, so income entries read as spending.

## What Changes

- Activity date-group headers show the **net total** for that day (income − expenses), signed: `-৳540` on a spend-heavy day, `+৳9,500` on a payday.
- The header total's color is bumped from tertiary faint to secondary muted so it is actually readable.
- Transaction rows become type-aware: income rows display `+৳X` in the positive (green) accent; expense rows keep the existing `-৳X` in primary ink. This applies everywhere the shared row component is used (Activity timeline and Home recent transactions).
- A signed money formatter is added to the shared formatting utilities.

## Capabilities

### New Capabilities

- `activity-day-totals`: Per-date net totals in the Activity timeline — how each day-group header computes and displays its signed total.
- `transaction-amount-display`: Type-aware signed amount rendering on transaction rows (income `+` green, expense `-` ink).

### Modified Capabilities

None — no existing specs in `openspec/specs/`.

## Impact

- `ui/screens/activity/ActivityViewModel.kt` — day total math switches to `signedAmount`.
- `ui/screens/activity/ActivityScreen.kt` — header uses signed formatting and a more readable color.
- `ui/components/TransactionItem.kt` — type-aware amount text/color; consumed by both `ActivityScreen` and `HomeScreen` (Home changes automatically, which is desired).
- `util/Formatters.kt` — new `formatSignedMoney` helper.
- No DB/schema, navigation, DI-registration, or dependency changes. `DayGroup.dayTotal` has no consumers outside the Activity screen, so changing its semantics to "net" is safe.
