package com.example.expensetracker.data

import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.Loan
import com.example.expensetracker.data.model.LoanDirection
import com.example.expensetracker.data.model.TransactionType
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * Seed transactions mirroring the design's sample household budget, spread across the current
 * month and the two before it so the screens look populated on first launch. Amounts come straight
 * from the design; dates are computed relative to "now" so the demo always lands in the right
 * range buckets.
 */
object SampleData {

    // monthsAgo -> (category -> monthly amount). Index 0 is the current month.
    private val monthly: List<Map<Category, Int>> = listOf(
        mapOf(
            Category.HOUSE_RENT to 1200, Category.GROCERY to 540, Category.EDUCATION to 480,
            Category.TRANSPORTATION to 320, Category.ELECTRICITY to 180, Category.MEDICINE to 165,
            Category.MEAT to 220, Category.FRESH_PRODUCT to 145, Category.FRUITS to 95,
            Category.GAS to 140, Category.UTILITY to 130, Category.CLOTHING to 210,
            Category.ELECTRONICS to 260, Category.HOSPITALITY to 240, Category.GIFT_DONATION to 60
        ),
        mapOf(
            Category.HOUSE_RENT to 1200, Category.GROCERY to 560, Category.EDUCATION to 480,
            Category.TRANSPORTATION to 210, Category.ELECTRICITY to 150, Category.MEDICINE to 90,
            Category.MEAT to 240, Category.FRESH_PRODUCT to 150, Category.FRUITS to 110,
            Category.GAS to 130, Category.UTILITY to 125, Category.CLOTHING to 80,
            Category.ELECTRONICS to 60, Category.HOSPITALITY to 150, Category.GIFT_DONATION to 120,
            Category.TRAINING to 70
        ),
        mapOf(
            Category.HOUSE_RENT to 1200, Category.GROCERY to 575, Category.EDUCATION to 480,
            Category.TRANSPORTATION to 205, Category.ELECTRICITY to 140, Category.MEDICINE to 85,
            Category.MEAT to 230, Category.FRESH_PRODUCT to 150, Category.FRUITS to 120,
            Category.GAS to 128, Category.UTILITY to 124, Category.CLOTHING to 70,
            Category.ELECTRONICS to 45, Category.HOSPITALITY to 130, Category.GIFT_DONATION to 40,
            Category.TRAINING to 70
        )
    )

    private val notes: Map<Category, List<String>> = mapOf(
        Category.GROCERY to listOf("Supermarket", "Weekly grocery"),
        Category.FRESH_PRODUCT to listOf("Veg & dairy", "Local market"),
        Category.FRUITS to listOf("Fruit stall", "Seasonal fruits"),
        Category.MEAT to listOf("Butcher", "Poultry & fish"),
        Category.MEDICINE to listOf("Pharmacy", "Prescription"),
        Category.EDUCATION to listOf("Kids tuition", "School supplies"),
        Category.TRAINING to listOf("Online course"),
        Category.HOUSE_RENT to listOf("Monthly rent"),
        Category.ELECTRICITY to listOf("Electric bill"),
        Category.GAS to listOf("Gas bill", "Fuel"),
        Category.UTILITY to listOf("Water & internet", "Water bill"),
        Category.CLOTHING to listOf("Kids clothes", "Apparel"),
        Category.ELECTRONICS to listOf("Headphones", "Phone charger"),
        Category.TRANSPORTATION to listOf("Ride-share", "Fuel"),
        Category.GIFT_DONATION to listOf("Charity", "Birthday gift"),
        Category.HOSPITALITY to listOf("Dinner out", "Hosting guests")
    )

    fun seed(nowMillis: Long = System.currentTimeMillis()): List<Expense> {
        val now = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val out = mutableListOf<Expense>()

        monthly.forEachIndexed { monthsAgo, map ->
            val base = (now.clone() as Calendar).apply { add(Calendar.MONTH, -monthsAgo) }
            val maxDay = if (monthsAgo == 0) {
                now.get(Calendar.DAY_OF_MONTH)
            } else {
                base.getActualMaximum(Calendar.DAY_OF_MONTH)
            }

            map.forEach { (category, amount) ->
                val labels = notes[category] ?: listOf("Expense")
                if (amount >= 200 && labels.size > 1) {
                    val first = (amount * 0.58).roundToInt()
                    out += expense(base, dayCapped(12, maxDay), category, first.toDouble(), labels[0])
                    out += expense(base, dayCapped(23, maxDay), category, (amount - first).toDouble(), labels[1])
                } else {
                    out += expense(base, dayCapped(17, maxDay), category, amount.toDouble(), labels[0])
                }
            }
        }
        return out
    }

    /**
     * Demo loans covering each card state: a partially-paid borrow, a due-soon borrow, an overdue
     * loan owed to the user, and a healthy/long-dated lent loan. Dates are relative to "now" so the
     * overdue / due-soon badges always render.
     */
    fun seedLoans(nowMillis: Long = System.currentTimeMillis()): List<Loan> {
        fun daysFromNow(days: Int): Long = nowMillis + days * 24L * 60 * 60 * 1000
        return listOf(
            Loan(
                person = "Rahim",
                direction = LoanDirection.BORROWED,
                principal = 800.0,
                paidAmount = 300.0,
                borrowedDate = daysFromNow(-40),
                dueDate = daysFromNow(20),
                note = "Emergency car repair"
            ),
            Loan(
                person = "Karim",
                direction = LoanDirection.BORROWED,
                principal = 450.0,
                paidAmount = 0.0,
                borrowedDate = daysFromNow(-25),
                dueDate = daysFromNow(4),
                note = "Short-term cash"
            ),
            Loan(
                person = "Salma",
                direction = LoanDirection.LENT,
                principal = 600.0,
                paidAmount = 150.0,
                borrowedDate = daysFromNow(-60),
                dueDate = daysFromNow(-3),
                note = "Tuition help"
            ),
            Loan(
                person = "Nadia",
                direction = LoanDirection.LENT,
                principal = 1000.0,
                paidAmount = 400.0,
                borrowedDate = daysFromNow(-30),
                dueDate = daysFromNow(45),
                note = "Business stock"
            )
        )
    }

    private fun dayCapped(day: Int, maxDay: Int) = day.coerceIn(1, maxDay)

    private fun expense(base: Calendar, day: Int, category: Category, amount: Double, title: String): Expense {
        val c = (base.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return Expense(
            title = title,
            amount = amount,
            category = category,
            type = TransactionType.EXPENSE,
            date = c.timeInMillis
        )
    }
}
