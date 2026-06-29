package com.example.expensetracker.ui.screens.common

import java.util.Calendar
import java.util.Locale

/** The three range presets shown in the date-range bottom sheet. */
enum class DateRange(val sheetLabel: String) {
    THIS_MONTH("This month"),
    LAST_MONTH("Last month"),
    LAST_3_MONTHS("Last 3 months")
}

/** Resolved boundaries + display strings for a [DateRange], computed against "now". */
data class RangeWindow(
    val rangeStart: Long,
    val rangeEnd: Long,
    val currentStart: Long,
    val currentEnd: Long,
    val previousStart: Long,
    val previousEnd: Long,
    val pill: String,
    val label: String,
    val comparisonLabel: String
) {
    fun inRange(millis: Long) = millis in rangeStart until rangeEnd
    fun inCurrent(millis: Long) = millis in currentStart until currentEnd
    fun inPrevious(millis: Long) = millis in previousStart until previousEnd
}

private fun monthStart(now: Calendar, monthsAgo: Int): Calendar =
    (now.clone() as Calendar).apply {
        add(Calendar.MONTH, -monthsAgo)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

private fun monthName(now: Calendar, monthsAgo: Int): String {
    val c = monthStart(now, monthsAgo)
    val month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)
    return "$month ${c.get(Calendar.YEAR)}"
}

private fun monthShort(now: Calendar, monthsAgo: Int): String =
    monthStart(now, monthsAgo).getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) ?: ""

fun DateRange.resolve(nowMillis: Long = System.currentTimeMillis()): RangeWindow {
    val now = Calendar.getInstance().apply { timeInMillis = nowMillis }
    fun start(monthsAgo: Int) = monthStart(now, monthsAgo).timeInMillis

    return when (this) {
        DateRange.THIS_MONTH -> RangeWindow(
            rangeStart = start(0), rangeEnd = start(-1),
            currentStart = start(0), currentEnd = start(-1),
            previousStart = start(1), previousEnd = start(0),
            pill = monthName(now, 0),
            label = monthName(now, 0),
            comparisonLabel = "vs ${monthShort(now, 1)}"
        )
        DateRange.LAST_MONTH -> RangeWindow(
            rangeStart = start(1), rangeEnd = start(0),
            currentStart = start(1), currentEnd = start(0),
            previousStart = start(2), previousEnd = start(1),
            pill = monthName(now, 1),
            label = monthName(now, 1),
            comparisonLabel = "vs ${monthShort(now, 2)}"
        )
        DateRange.LAST_3_MONTHS -> RangeWindow(
            rangeStart = start(2), rangeEnd = start(-1),
            currentStart = start(0), currentEnd = start(-1),
            previousStart = start(1), previousEnd = start(0),
            pill = "Last 3 months",
            label = "${monthShort(now, 2)} – ${monthShort(now, 0)} ${now.get(Calendar.YEAR)}",
            comparisonLabel = "${monthShort(now, 0)} vs ${monthShort(now, 1)}"
        )
    }
}
