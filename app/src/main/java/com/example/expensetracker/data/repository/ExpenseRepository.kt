package com.example.expensetracker.data.repository

import com.example.expensetracker.data.SampleData
import com.example.expensetracker.data.local.ExpenseDao
import com.example.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow

/** Thin abstraction over the DAO so ViewModels never touch Room directly. */
class ExpenseRepository(private val dao: ExpenseDao) {

    val transactions: Flow<List<Expense>> = dao.observeAll()

    /** Populate the demo dataset the first time the app runs with an empty database. */
    suspend fun seedIfEmpty() {
        if (dao.count() == 0) {
            dao.insertAll(SampleData.seed())
        }
    }

    suspend fun getById(id: Long): Expense? = dao.getById(id)

    suspend fun add(expense: Expense): Long = dao.insert(expense)

    suspend fun update(expense: Expense) = dao.update(expense)

    suspend fun delete(expense: Expense) = dao.delete(expense)
}
