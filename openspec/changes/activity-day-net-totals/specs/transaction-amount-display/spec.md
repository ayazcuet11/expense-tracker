# Spec: transaction-amount-display

## ADDED Requirements

### Requirement: Transaction row amounts are type-aware
The shared transaction row component SHALL render the amount according to the transaction's type: income rows MUST show `+৳X` in the positive accent color (`PositiveText`), and expense rows MUST show `-৳X` in the primary ink color. This behavior SHALL apply everywhere the component is used, including the Activity timeline and the Home screen's recent transactions.

#### Scenario: Expense row
- **WHEN** a transaction of type EXPENSE with amount ৳340 is rendered
- **THEN** the row shows `-৳340` in the primary ink color

#### Scenario: Income row
- **WHEN** a transaction of type INCOME with amount ৳10,000 is rendered
- **THEN** the row shows `+৳10,000` in the positive (green) accent color

#### Scenario: Consistency across screens
- **WHEN** the same income transaction appears in both the Activity timeline and the Home recent-transactions list
- **THEN** both rows show the identical `+৳X` green rendering

### Requirement: Shared signed money formatter
The formatting utilities SHALL provide a `formatSignedMoney(amount)` function that returns `+৳N` for positive amounts, `-৳N` for negative amounts, and `৳0` (no sign) when the amount rounds to zero, using the same grouped whole-unit formatting as `formatMoney`.

#### Scenario: Positive amount
- **WHEN** `formatSignedMoney(9500.0)` is called
- **THEN** it returns `+৳9,500`

#### Scenario: Negative amount
- **WHEN** `formatSignedMoney(-540.0)` is called
- **THEN** it returns `-৳540`

#### Scenario: Rounds to zero
- **WHEN** `formatSignedMoney(-0.4)` is called
- **THEN** it returns `৳0` without a sign prefix
