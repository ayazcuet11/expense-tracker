package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Money owed in either direction. [principal] and [paidAmount] are always positive; [paidAmount]
 * accumulates as payments are recorded (clamped to the principal). [direction] decides whether the
 * remaining balance is something the user owes (BORROWED) or is owed (LENT).
 */
@Entity(tableName = "loans")
data class Loan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val person: String,
    val direction: LoanDirection,
    val principal: Double,
    val paidAmount: Double = 0.0,
    /** Epoch millis of when the loan was taken / given. */
    val borrowedDate: Long = System.currentTimeMillis(),
    /** Optional epoch-millis repayment deadline. */
    val dueDate: Long? = null,
    val note: String = ""
) {
    val remaining: Double
        get() = (principal - paidAmount).coerceAtLeast(0.0)

    val progress: Float
        get() = if (principal <= 0.0) 0f else (paidAmount / principal).toFloat().coerceIn(0f, 1f)

    val isSettled: Boolean
        get() = remaining <= 0.0

    /** Status as of [nowMillis]; SETTLED wins, then OVERDUE / DUE_SOON for dated loans. */
    fun status(nowMillis: Long): LoanStatus = when {
        isSettled -> LoanStatus.SETTLED
        dueDate == null -> LoanStatus.ACTIVE
        dueDate < nowMillis -> LoanStatus.OVERDUE
        dueDate - nowMillis <= DUE_SOON_WINDOW_MILLIS -> LoanStatus.DUE_SOON
        else -> LoanStatus.ACTIVE
    }

    private companion object {
        const val DUE_SOON_WINDOW_MILLIS = 7L * 24 * 60 * 60 * 1000
    }
}
