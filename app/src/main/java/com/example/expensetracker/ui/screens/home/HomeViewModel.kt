package com.example.expensetracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.screens.common.CategoryStat
import com.example.expensetracker.ui.screens.common.DateRange
import com.example.expensetracker.ui.screens.common.Movement
import com.example.expensetracker.ui.screens.common.buildRangeReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

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
    val isEmpty: Boolean = true
)

class HomeViewModel(repository: ExpenseRepository) : ViewModel() {

    private val range = MutableStateFlow(DateRange.THIS_MONTH)

    val uiState: StateFlow<HomeUiState> =
        combine(repository.transactions, range) { transactions, selected ->
            val report = buildRangeReport(transactions, selected, System.currentTimeMillis())
            val top = report.byCategory.take(3)
            val other = report.byCategory.drop(3).sumOf { it.amount }
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
                isEmpty = report.isEmpty
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun setRange(value: DateRange) {
        range.value = value
    }
}
