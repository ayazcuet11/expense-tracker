## ADDED Requirements

### Requirement: Transaction rows expose a delete button
Every transaction row (Home recent list and Activity list) SHALL display a trash-icon delete
button as a trailing element of the row. The delete button SHALL be its own touch target:
tapping it MUST NOT trigger the row's click action (opening the transaction in the edit screen).

#### Scenario: Delete icon visible on each row
- **WHEN** a transaction row is rendered on the Home or Activity screen
- **THEN** a trash icon appears at the trailing edge of the row

#### Scenario: Delete tap does not open the edit screen
- **WHEN** the user taps the trash icon on a transaction row
- **THEN** the confirmation dialog opens and the app does NOT navigate to the edit screen

### Requirement: Loan cards expose a delete button
Every loan card on the Loans screen SHALL display a trash-icon delete button. Tapping it
MUST NOT trigger any other card action (such as "record payment").

#### Scenario: Delete icon visible on each loan card
- **WHEN** a loan card is rendered on the Loans screen (any tab, active or settled)
- **THEN** a trash icon is visible on the card

### Requirement: Deletion requires confirmation naming the record
Tapping a delete button SHALL open a confirmation dialog before anything is removed. The dialog
SHALL name exactly what will be removed — the category and formatted amount for a transaction,
the person and formatted principal for a loan — and SHALL offer Cancel and Delete actions.

#### Scenario: Transaction confirmation names category and amount
- **WHEN** the user taps delete on a transaction row for a $24.50 Groceries expense
- **THEN** a dialog appears whose text includes "Groceries" and "$24.50" with Cancel and Delete actions

#### Scenario: Loan confirmation names person and principal
- **WHEN** the user taps delete on a loan card for a $500 loan with Sarah
- **THEN** a dialog appears whose text includes "Sarah" and "$500.00" with Cancel and Delete actions

#### Scenario: Cancel keeps the record
- **WHEN** the user taps Cancel (or dismisses the dialog by tapping outside it)
- **THEN** the dialog closes and the record still exists

### Requirement: Confirming removes the record everywhere
Confirming Delete SHALL remove the record from the database. Because all screens derive their
state from the same reactive flows, the record SHALL disappear from every list, total, and
breakdown that included it.

#### Scenario: Deleted transaction disappears and totals update
- **WHEN** the user confirms deletion of a transaction
- **THEN** the row disappears from the Home and Activity lists and the spent totals/category breakdowns recompute without it

#### Scenario: Deleted loan disappears and summary tiles update
- **WHEN** the user confirms deletion of a loan
- **THEN** the loan card disappears and the owe/lent remaining totals and counts recompute without it
