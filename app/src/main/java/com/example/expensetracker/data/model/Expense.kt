package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single money movement. Despite the app name, this represents both income and expenses;
 * the [type] field distinguishes them and [amount] is always stored as a positive value.
 */
@Entity(tableName = "transactions")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: Category,
    val type: TransactionType,
    /** Epoch millis of when the transaction occurred. */
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
) {
    /** Amount with sign applied: positive for income, negative for expense. */
    val signedAmount: Double
        get() = if (type == TransactionType.INCOME) amount else -amount
}
