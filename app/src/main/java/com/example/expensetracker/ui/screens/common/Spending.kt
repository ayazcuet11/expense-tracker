package com.example.expensetracker.ui.screens.common

import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.util.formatMoney
import kotlin.math.abs
import kotlin.math.roundToInt

/** One category's slice of the selected range. */
data class CategoryStat(
    val category: Category,
    val amount: Double,
    val fractionOfTotal: Float,
    val fractionOfMax: Float,
    val percent: Int
)

/** A day's worth of transactions for the Activity timeline. */
data class DayGroup(
    val dayLabel: String,
    val dayTotal: Double,
    val transactions: List<Expense>
)

/** A category that moved versus the previous month. */
data class Movement(
    val category: Category,
    val delta: Double,
    val deltaLabel: String,
    val percent: Int,
    val currentAmount: Double,
    val rising: Boolean
)

/** Everything the spending screens need for a resolved range. */
data class RangeReport(
    val window: RangeWindow,
    val total: Double,
    val count: Int,
    val deltaPercent: Int,
    val deltaUp: Boolean,
    val byCategory: List<CategoryStat>,
    val rising: List<Movement>,
    val falling: List<Movement>,
    val recent: List<Expense>,
    val isEmpty: Boolean
)

/** Build the full report for [range] from the complete transaction list. */
fun buildRangeReport(all: List<Expense>, range: DateRange, nowMillis: Long): RangeReport {
    val window = range.resolve(nowMillis)
    val inRange = all.filter { window.inRange(it.date) }
    val total = inRange.sumOf { it.amount }

    val byCategoryAmount = inRange.groupBy { it.category }
        .mapValues { (_, items) -> items.sumOf { it.amount } }
    val maxAmount = byCategoryAmount.values.maxOrNull() ?: 1.0
    val byCategory = byCategoryAmount.entries
        .map { (category, amount) ->
            CategoryStat(
                category = category,
                amount = amount,
                fractionOfTotal = if (total > 0) (amount / total).toFloat() else 0f,
                fractionOfMax = (amount / maxAmount).toFloat(),
                percent = if (total > 0) (amount / total * 100).roundToInt() else 0
            )
        }
        .sortedByDescending { it.amount }

    // Month-over-month movement.
    val currentByCat = all.filter { window.inCurrent(it.date) }
        .groupBy { it.category }.mapValues { (_, v) -> v.sumOf { it.amount } }
    val previousByCat = all.filter { window.inPrevious(it.date) }
        .groupBy { it.category }.mapValues { (_, v) -> v.sumOf { it.amount } }

    val rising = mutableListOf<Movement>()
    val falling = mutableListOf<Movement>()
    (currentByCat.keys + previousByCat.keys).distinct().forEach { category ->
        val cur = currentByCat[category] ?: 0.0
        val prev = previousByCat[category] ?: 0.0
        val delta = cur - prev
        if (delta > 0) {
            rising += Movement(
                category = category,
                delta = delta,
                deltaLabel = "+" + formatMoney(delta),
                percent = if (prev > 0) (delta / prev * 100).roundToInt() else 100,
                currentAmount = cur,
                rising = true
            )
        } else if (delta < 0) {
            falling += Movement(
                category = category,
                delta = delta,
                deltaLabel = "-" + formatMoney(abs(delta)),
                percent = if (prev > 0) (abs(delta) / prev * 100).roundToInt() else 0,
                currentAmount = cur,
                rising = false
            )
        }
    }
    rising.sortByDescending { it.delta }
    falling.sortBy { it.delta }

    val currentTotal = currentByCat.values.sum()
    val previousTotal = previousByCat.values.sum()
    val deltaPercent = if (previousTotal > 0) {
        ((currentTotal - previousTotal) / previousTotal * 100).roundToInt()
    } else 0

    val recent = inRange.sortedByDescending { it.date }.take(4)

    return RangeReport(
        window = window,
        total = total,
        count = inRange.size,
        deltaPercent = abs(deltaPercent),
        deltaUp = currentTotal >= previousTotal,
        byCategory = byCategory,
        rising = rising,
        falling = falling,
        recent = recent,
        isEmpty = inRange.isEmpty()
    )
}
