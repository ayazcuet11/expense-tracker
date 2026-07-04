# Spec: add-transaction-form

## ADDED Requirements

### Requirement: Date field on the Add/Edit Expense screen
The Add/Edit Expense screen SHALL display a date field showing the transaction's currently
selected date in a human-readable format. For a new transaction the field SHALL default to
today's date. Tapping the field SHALL open a Material 3 date picker; confirming a selection
SHALL update the displayed date, and dismissing the picker SHALL leave it unchanged.

#### Scenario: Default date for a new transaction
- **WHEN** the user opens the Add Expense screen to create a new transaction
- **THEN** the date field shows today's date

#### Scenario: Picking a past date
- **WHEN** the user taps the date field, selects a past date in the picker, and confirms
- **THEN** the date field shows the selected date and saving persists the transaction with that date

#### Scenario: Dismissing the picker
- **WHEN** the user opens the date picker and dismisses it without confirming
- **THEN** the previously selected date is retained

#### Scenario: Editing pre-fills the stored date
- **WHEN** the user opens an existing transaction in edit mode
- **THEN** the date field shows that transaction's stored date, and saving without touching the field preserves it

### Requirement: Details field on the Add/Edit Expense screen
The Add/Edit Expense screen SHALL provide a free-text details input persisted to the
transaction's `note` column. The field SHALL be optional — a transaction MUST be saveable with
the details field empty, and an empty field SHALL persist as an empty string.

#### Scenario: Saving with details
- **WHEN** the user enters text in the details field and saves the transaction
- **THEN** the transaction is persisted with that text in its `note` column

#### Scenario: Saving without details
- **WHEN** the user saves a transaction leaving the details field empty
- **THEN** the save succeeds and the transaction's `note` is an empty string

#### Scenario: Editing pre-fills stored details
- **WHEN** the user opens an existing transaction in edit mode
- **THEN** the details field shows that transaction's stored note, and saving preserves or updates it with the field's current content

### Requirement: Date changes carry a stable time-of-day
When the user picks a calendar date, the system SHALL store an epoch-millis timestamp that
falls on the chosen calendar day in the device's local time zone, so downstream day-grouping
(Activity, Stats, Insights) buckets the transaction under the picked day.

#### Scenario: Back-dated entry appears under its day in Activity
- **WHEN** the user saves a transaction dated three days ago
- **THEN** the Activity screen groups it under that day, not today
