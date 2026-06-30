package com.example.expensetracker.data.model

/**
 * Lifecycle state of a [Loan], derived from its remaining balance and due date (see
 * [Loan.status]). The [label] doubles as the badge text shown on loan cards and reminders.
 */
enum class LoanStatus(val label: String) {
    ACTIVE("On track"),
    DUE_SOON("Due soon"),
    OVERDUE("Overdue"),
    SETTLED("Settled")
}
