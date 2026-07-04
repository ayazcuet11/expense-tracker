package com.example.expensetracker.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.screens.common.DateRange
import com.example.expensetracker.ui.screens.common.DayGroup
import com.example.expensetracker.ui.screens.common.buildRangeReport
import com.example.expensetracker.util.formatDayLabel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ActivityUiState(
    val range: DateRange = DateRange.THIS_MONTH,
    val rangePill: String = "",
    val groups: List<DayGroup> = emptyList(),
    val isEmpty: Boolean = true
)

class ActivityViewModel(repository: ExpenseRepository) : ViewModel() {

    private val range = MutableStateFlow(DateRange.THIS_MONTH)

    val uiState: StateFlow<ActivityUiState> =
        combine(repository.transactions, range) { transactions, selected ->
            val report = buildRangeReport(transactions, selected, System.currentTimeMillis())
            val inRange = transactions
                .filter { report.window.inRange(it.date) }
                .sortedByDescending { it.date }
            val groups = inRange
                .groupBy { formatDayLabel(it.date) }
                .map { (label, items) ->
                    DayGroup(
                        dayLabel = label,
                        dayTotal = items.sumOf { it.signedAmount },
                        transactions = items
                    )
                }
            ActivityUiState(
                range = selected,
                rangePill = report.window.pill,
                groups = groups,
                isEmpty = inRange.isEmpty()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActivityUiState()
        )

    fun setRange(value: DateRange) {
        range.value = value
    }
}
