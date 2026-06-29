package com.example.expensetracker.data.local

import androidx.room.TypeConverter
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType

/** Persists enums as their stable name() string so reordering enum entries stays safe. */
class Converters {
    @TypeConverter
    fun fromCategory(value: Category): String = value.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)
}
