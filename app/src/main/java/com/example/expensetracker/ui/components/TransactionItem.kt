package com.example.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.theme.Divider
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkFaint
import com.example.expensetracker.ui.theme.PositiveText
import com.example.expensetracker.util.formatExpense
import com.example.expensetracker.util.formatMoney

/** A single transaction row: monogram badge, category + note, and the expense amount. */
@Composable
fun TransactionItem(
    expense: Expense,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .padding(vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryBadge(category = expense.category)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val subtitle = expense.title.ifBlank { expense.note }
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = InkFaint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            val isIncome = expense.type == TransactionType.INCOME
            Text(
                text = if (isIncome) "+" + formatMoney(expense.amount) else formatExpense(expense.amount),
                style = MaterialTheme.typography.titleSmall,
                color = if (isIncome) PositiveText else Ink
            )
        }
        if (showDivider) {
            HorizontalDivider(color = Divider, thickness = 1.dp)
        }
    }
}
