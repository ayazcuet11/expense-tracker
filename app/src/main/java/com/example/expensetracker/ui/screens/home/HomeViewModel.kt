package com.example.expensetracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.LoanDirection
import com.example.expensetracker.data.model.LoanStatus
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.screens.common.CategoryStat
import com.example.expensetracker.ui.screens.common.DateRange
import com.example.expensetracker.ui.screens.common.Movement
import com.example.expensetracker.ui.screens.common.buildRangeReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val range: DateRange = DateRange.THIS_MONTH,
    val rangePill: String = "",
    val comparisonLabel: String = "",
    val total: Double = 0.0,
    val count: Int = 0,
    val deltaPercent: Int = 0,
    val deltaUp: Boolean = true,
    val topCategories: List<CategoryStat> = emptyList(),
    val otherAmount: Double = 0.0,
    val rising: List<Movement> = emptyList(),
    val recent: List<Expense> = emptyList(),
    val ideasCount: Int = 0,
    val isEmpty: Boolean = true,
    val oweRemaining: Double = 0.0,
    val lentRemaining: Double = 0.0,
    val oweCount: Int = 0,
    val lentCount: Int = 0,
    val overdueCount: Int = 0,
    val hasLoans: Boolean = false
)

class HomeViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val range = MutableStateFlow(DateRange.THIS_MONTH)

    val uiState: StateFlow<HomeUiState> =
        combine(repository.transactions, range, repository.loans) { transactions, selected, loans ->
            val now = System.currentTimeMillis()
            val report = buildRangeReport(transactions, selected, now)
            val top = report.byCategory.take(3)
            val other = report.byCategory.drop(3).sumOf { it.amount }
            val active = loans.filterNot { it.isSettled }
            val (owe, lent) = active.partition { it.direction == LoanDirection.BORROWED }
            HomeUiState(
                range = selected,
                rangePill = report.window.pill,
                comparisonLabel = report.window.comparisonLabel,
                total = report.total,
                count = report.count,
                deltaPercent = report.deltaPercent,
                deltaUp = report.deltaUp,
                topCategories = top,
                otherAmount = other,
                rising = report.rising.take(3),
                recent = report.recent,
                // Three "adjust" cards plus four "worth adding" ideas, matching the design CTA.
                ideasCount = report.rising.take(3).size + 4,
                isEmpty = report.isEmpty,
                oweRemaining = owe.sumOf { it.remaining },
                lentRemaining = lent.sumOf { it.remaining },
                oweCount = owe.size,
                lentCount = lent.size,
                overdueCount = loans.count { it.status(now) == LoanStatus.OVERDUE },
                hasLoans = loans.isNotEmpty()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun setRange(value: DateRange) {
        range.value = value
    }

    /** Transaction awaiting delete confirmation, or null when no dialog is showing. */
    private val _pendingDelete = MutableStateFlow<Expense?>(null)
    val pendingDelete: StateFlow<Expense?> = _pendingDelete.asStateFlow()

    fun requestDelete(expense: Expense) {
        _pendingDelete.value = expense
    }

    fun cancelDelete() {
        _pendingDelete.value = null
    }

    fun confirmDelete() {
        val expense = _pendingDelete.value ?: return
        viewModelScope.launch {
            repository.delete(expense)
            _pendingDelete.value = null
        }
    }
}
