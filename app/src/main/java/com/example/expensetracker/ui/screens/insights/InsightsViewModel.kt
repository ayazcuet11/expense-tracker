package com.example.expensetracker.ui.screens.insights

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.screens.common.DateRange
import com.example.expensetracker.ui.screens.common.buildRangeReport
import com.example.expensetracker.util.formatMoney
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** A "rising spend" coaching card. */
data class AdjustTip(
    val category: Category,
    val stat: String,
    val tip: String
)

/** A "worth adding" suggestion — not tied to an existing transaction category. */
data class AddIdea(
    val title: String,
    val subtitle: String,
    val mono: String,
    val color: Color,
    val tip: String,
    val cta: String
)

/** A discretionary line item suggested for trimming. */
data class TrimItem(
    val category: Category,
    val line: String,
    val sub: String
)

data class InsightsUiState(
    val adjust: List<AdjustTip> = emptyList(),
    val add: List<AddIdea> = emptyList(),
    val trim: List<TrimItem> = emptyList()
)

class InsightsViewModel(repository: ExpenseRepository) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = repository.transactions
        .map { transactions ->
            val report = buildRangeReport(transactions, DateRange.THIS_MONTH, System.currentTimeMillis())
            val rising = report.rising

            val adjust = rising.take(3).map { m ->
                AdjustTip(
                    category = m.category,
                    stat = "+${m.percent}% · ${m.deltaLabel}",
                    tip = adjustTips[m.category]
                        ?: "Up ${m.percent}% versus last month. Review recent purchases in this category."
                )
            }

            val discretionary = setOf(
                Category.HOSPITALITY, Category.ELECTRONICS, Category.CLOTHING, Category.GIFT_DONATION
            )
            val trim = rising.filter { it.category in discretionary }.take(3).map { m ->
                TrimItem(
                    category = m.category,
                    line = "${m.category.label} is ${m.percent}% above last month",
                    sub = "Now ${formatMoney(m.currentAmount)} · discretionary"
                )
            }

            InsightsUiState(adjust = adjust, add = staticAddIdeas, trim = trim)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InsightsUiState()
        )

    private val adjustTips = mapOf(
        Category.TRANSPORTATION to "Rides and fuel climbed sharply this month. Batch errands into fewer trips or try a weekly transit pass to cap it.",
        Category.ELECTRONICS to "A large jump — likely one-off gadgets. Pause new purchases for 30 days before buying to avoid impulse spend.",
        Category.CLOTHING to "Clothing more than doubled. Set a seasonal family clothing budget so it stays predictable.",
        Category.HOSPITALITY to "Dining out and hosting rose. Aim for one fewer restaurant night each week and host potluck-style.",
        Category.MEDICINE to "Medical costs nearly doubled. Check what was one-time vs recurring, and keep a small health buffer.",
        Category.ELECTRICITY to "Bill ticked up with the season. Run heavy appliances off-peak and check cooling settings."
    )

    private val staticAddIdeas = listOf(
        AddIdea(
            title = "Start a savings habit",
            subtitle = "Savings",
            mono = "Sv",
            color = Color(0xFF2F7A57),
            tip = "You set aside nothing this month. Auto-transfer 10% of income before you spend — your family's safety net.",
            cta = "Set aside ~${formatMoney(420.0)}"
        ),
        AddIdea(
            title = "Invest in a skill",
            subtitle = "Skill courses",
            mono = "Sk",
            color = Color(0xFF5B5FC7),
            tip = "Little training spend this month. One course a quarter compounds into career growth and higher income.",
            cta = "Budget ${formatMoney(60.0)}/mo"
        ),
        AddIdea(
            title = "Preventive health",
            subtitle = "Health checkups",
            mono = "Hc",
            color = Color(0xFF3E8E8A),
            tip = "Spending shows medicine but no check-ups. Book annual screenings for you and the kids before issues grow.",
            cta = "Plan a checkup"
        ),
        AddIdea(
            title = "Keep learning funded",
            subtitle = "Kids education",
            mono = "Ed",
            color = Color(0xFF3D6FA8),
            tip = "Education is steady — add a small monthly fund for books and activities so it scales as they grow.",
            cta = "Add a fund"
        )
    )
}
