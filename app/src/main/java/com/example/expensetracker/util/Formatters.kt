package com.example.expensetracker.util

import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

private val groupedFormat: NumberFormat = NumberFormat.getIntegerInstance(Locale.US)

/** "$1,250" — rounded to whole units with grouping, matching the design's figures. */
fun formatMoney(amount: Double): String = "৳" + groupedFormat.format(amount.roundToLong())

/** Expense display with a leading minus, e.g. "-$540". */
fun formatExpense(amount: Double): String = "-" + formatMoney(amount)

/** Signed display, e.g. "+$1,200" / "-$540"; "$0" (unsigned) when the amount rounds to zero. */
fun formatSignedMoney(amount: Double): String {
    val rounded = amount.roundToLong()
    val sign = if (rounded > 0) "+" else if (rounded < 0) "-" else ""
    return sign + "৳" + groupedFormat.format(abs(rounded))
}

/** "$1,250" — alias kept for older call sites. */
fun formatCurrency(amount: Double): String = formatMoney(amount)

/** Human-friendly day label: Today / Yesterday / "Jun 24". */
fun formatDayLabel(epochMillis: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = epochMillis }
    val now = Calendar.getInstance()

    fun sameDay(a: Calendar, b: Calendar) =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

    val yesterday = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
    return when {
        sameDay(cal, now) -> "Today"
        sameDay(cal, yesterday) -> "Yesterday"
        else -> {
            val month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
            "$month ${cal.get(Calendar.DAY_OF_MONTH)}"
        }
    }
}

/** Short clock time, e.g. "2:30 PM". */
fun formatTime(epochMillis: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = epochMillis }
    var hour = cal.get(Calendar.HOUR)
    if (hour == 0) hour = 12
    val minute = cal.get(Calendar.MINUTE)
    val amPm = if (cal.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
    return "%d:%02d %s".format(hour, minute, amPm)
}
