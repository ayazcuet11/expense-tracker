# Spec: activity-day-totals

## ADDED Requirements

### Requirement: Activity date headers show the day's net total
The Activity timeline SHALL display, on the right side of each date-group header, the net total of that day's transactions, computed as the sum of signed amounts (income positive, expense negative) of all transactions in the group.

#### Scenario: Expense-only day
- **WHEN** a date group contains only expenses totalling ৳540
- **THEN** the header shows `-৳540` on the right

#### Scenario: Income-dominant day
- **WHEN** a date group contains ৳10,000 income and ৳500 of expenses
- **THEN** the header shows `+৳9,500` on the right

#### Scenario: Day nets to zero
- **WHEN** a date group's income and expenses cancel out exactly
- **THEN** the header shows `৳0` with no sign prefix

### Requirement: Day total is signed and readable
The day total SHALL be rendered with an explicit `+` or `-` sign prefix (except zero) and MUST use the secondary muted text color (`InkMuted`), matching the date label, rather than the tertiary faint color.

#### Scenario: Header legibility
- **WHEN** a date group header is rendered
- **THEN** the total on the right uses the same `labelLarge` typography and `InkMuted` color as the date label on the left
