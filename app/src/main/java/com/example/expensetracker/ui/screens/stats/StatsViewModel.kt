package com.example.expensetracker.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class StatsUiState(
    val range: DateRange = DateRange.THIS_MONTH,
    val rangeLabel: String = "",
    val comparisonLabel: String = "",
    val total: Double = 0.0,
    val deltaPercent: Int = 0,
    val deltaUp: Boolean = true,
    val breakdown: List<CategoryStat> = emptyList(),
    val rising: List<Movement> = emptyList(),
    val falling: List<Movement> = emptyList(),
    val isEmpty: Boolean = true
)

class StatsViewModel(repository: ExpenseRepository) : ViewModel() {

    private val range = MutableStateFlow(DateRange.THIS_MONTH)

    val uiState: StateFlow<StatsUiState> =
        combine(repository.transactions, range) { transactions, selected ->
            val report = buildRangeReport(transactions, selected, System.currentTimeMillis())
            StatsUiState(
                range = selected,
                rangeLabel = report.window.label,
                comparisonLabel = report.window.comparisonLabel,
                total = report.total,
                deltaPercent = report.deltaPercent,
                deltaUp = report.deltaUp,
                breakdown = report.byCategory,
                rising = report.rising.take(4),
                falling = report.falling.take(2),
                isEmpty = report.isEmpty
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsUiState()
        )

    fun setRange(value: DateRange) {
        range.value = value
    }
}
