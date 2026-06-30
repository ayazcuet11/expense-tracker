package com.example.expensetracker.data.repository

import com.example.expensetracker.data.SampleData
import com.example.expensetracker.data.local.ExpenseDao
import com.example.expensetracker.data.local.LoanDao
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.Loan
import kotlinx.coroutines.flow.Flow

/** Thin abstraction over the DAOs so ViewModels never touch Room directly. */
class ExpenseRepository(
    private val dao: ExpenseDao,
    private val loanDao: LoanDao
) {

    val transactions: Flow<List<Expense>> = dao.observeAll()

    val loans: Flow<List<Loan>> = loanDao.observeAll()

    /** Populate the demo dataset the first time the app runs with an empty database. */
    suspend fun seedIfEmpty() {
        if (dao.count() == 0) {
            dao.insertAll(SampleData.seed())
        }
        if (loanDao.count() == 0) {
            loanDao.insertAll(SampleData.seedLoans())
        }
    }

    suspend fun getById(id: Long): Expense? = dao.getById(id)

    suspend fun add(expense: Expense): Long = dao.insert(expense)

    suspend fun update(expense: Expense) = dao.update(expense)

    suspend fun delete(expense: Expense) = dao.delete(expense)

    // --- Loans ---

    suspend fun getLoan(id: Long): Loan? = loanDao.getById(id)

    suspend fun addLoan(loan: Loan): Long = loanDao.insert(loan)

    suspend fun updateLoan(loan: Loan) = loanDao.update(loan)

    suspend fun deleteLoan(loan: Loan) = loanDao.delete(loan)

    /** Adds [amount] to the loan's paid total, clamped so it never exceeds the principal. */
    suspend fun recordPayment(loanId: Long, amount: Double) {
        if (amount <= 0.0) return
        val loan = loanDao.getById(loanId) ?: return
        val newPaid = (loan.paidAmount + amount).coerceIn(0.0, loan.principal)
        loanDao.update(loan.copy(paidAmount = newPaid))
    }
}
