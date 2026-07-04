# Spec: add-amount-display

## ADDED Requirements

### Requirement: Amount display is always a single line
The Add/Edit expense screen SHALL render the in-progress amount on exactly one line for every reachable input state (up to 9 digits plus an optional decimal point), regardless of which digits are entered. The text MUST NOT wrap onto a second line or overlap the currency symbol.

#### Scenario: Maximum-width digits at the input cap
- **WHEN** the user has entered `479999999` (9 digits, including the widest glyphs)
- **THEN** `৳479999999` renders on a single line with no character on a second line

#### Scenario: Decimal at the cap
- **WHEN** the user has entered `4799999.99` (9 digits plus decimal point)
- **THEN** the full amount renders on a single line

### Requirement: Font size adapts to amount length
The amount display SHALL use a font size determined solely by the display string's length: the current 52.sp for short amounts (display string ≤ 9 characters), stepping down for longer strings so the maximum-length amount fits the screen width. The mapping MUST be deterministic — the same string always renders at the same size, with no measure-and-retry flicker.

#### Scenario: Short amount keeps current look
- **WHEN** the display string is `৳479` (4 characters)
- **THEN** it renders at 52.sp, identical to current behavior

#### Scenario: Long amount steps down
- **WHEN** the display string exceeds 9 characters
- **THEN** it renders at a reduced font size such that the full string fits within the screen width
