package com.example.expensetracker.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.components.CategoryBadge
import com.example.expensetracker.ui.components.CategoryDonut
import com.example.expensetracker.ui.components.ConfirmDeleteDialog
import com.example.expensetracker.ui.components.DeltaChip
import com.example.expensetracker.ui.components.RangePill
import com.example.expensetracker.ui.components.RangeSheet
import com.example.expensetracker.ui.components.SectionCard
import com.example.expensetracker.ui.components.TransactionItem
import com.example.expensetracker.ui.components.UserDisplayName
import com.example.expensetracker.ui.screens.common.CategoryStat
import com.example.expensetracker.ui.screens.common.Movement
import com.example.expensetracker.ui.theme.AccentSurface
import com.example.expensetracker.ui.theme.DangerSurface
import com.example.expensetracker.ui.theme.DangerText
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkFaint
import com.example.expensetracker.ui.theme.InkMuted
import com.example.expensetracker.ui.theme.InkSoft
import com.example.expensetracker.ui.theme.OnInk
import com.example.expensetracker.ui.theme.OnInkMuted
import com.example.expensetracker.ui.theme.PositiveText
import com.example.expensetracker.ui.theme.Track
import com.example.expensetracker.util.formatExpense
import com.example.expensetracker.util.formatMoney

private val OtherColor = Color(0xFFCFC8BA)

@Composable
fun HomeScreen(
    onTransactionClick: (Long) -> Unit,
    onSeeStats: () -> Unit,
    onSeeActivity: () -> Unit,
    onSeeInsights: () -> Unit,
    onOpenLoans: () -> Unit,
    onAvatarClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pendingDelete by viewModel.pendingDelete.collectAsStateWithLifecycle()
    var sheetOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RangePill(label = state.rangePill, onClick = { sheetOpen = true })
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Ink)
                        .clickable(onClick = onAvatarClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = UserDisplayName.first().uppercase(),
                        color = OnInk,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(top = 2.dp)) {
                Text(
                    text = "TOTAL SPENT",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkFaint
                )
                Text(
                    text = formatMoney(state.total),
                    style = MaterialTheme.typography.displayLarge,
                    color = Ink,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    DeltaChip(
                        up = state.deltaUp,
                        percent = state.deltaPercent,
                        textColor = if (state.deltaUp) DangerText else PositiveText,
                        backgroundColor = if (state.deltaUp) DangerSurface else AccentSurface
                    )
                    Text(
                        text = "${state.comparisonLabel} · ${state.count} purchases",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted
                    )
                }
            }
        }

        if (state.topCategories.isNotEmpty()) {
            item { TopCategoriesCard(state, onSeeStats) }
        }

        if (state.rising.isNotEmpty()) {
            item { RisingCard(state.rising, state.comparisonLabel) }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent", style = MaterialTheme.typography.titleMedium, color = Ink)
                LinkText("See all", onSeeActivity)
            }
        }

        item {
            if (state.recent.isEmpty()) {
                EmptyState()
            } else {
                SectionCard(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    state.recent.forEachIndexed { index, expense ->
                        TransactionItem(
                            expense = expense,
                            onClick = { onTransactionClick(expense.id) },
                            showDivider = index != state.recent.lastIndex,
                            onDelete = { viewModel.requestDelete(expense) }
                        )
                    }
                }
            }
        }

        if (state.hasLoans) {
            item { LoansSummaryCard(state, onOpenLoans) }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Ink)
                    .clickable(onClick = onSeeInsights)
                    .padding(horizontal = 18.dp, vertical = 17.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${state.ideasCount} ideas for your family & future",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnInk
                    )
                    Text(
                        text = "Where to adjust, what to add, what to trim",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnInkMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = OnInk
                )
            }
        }
    }

    if (sheetOpen) {
        RangeSheet(
            selected = state.range,
            onSelect = {
                viewModel.setRange(it)
                sheetOpen = false
            },
            onDismiss = { sheetOpen = false }
        )
    }

    pendingDelete?.let { expense ->
        ConfirmDeleteDialog(
            title = "Delete transaction?",
            text = "This will remove ${expense.category.label} — ${formatMoney(expense.amount)}.",
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::cancelDelete
        )
    }
}

@Composable
private fun LoansSummaryCard(state: HomeUiState, onClick: () -> Unit) {
    val overdueColor = if (state.overdueCount > 0) DangerText else PositiveText
    val overdueNote = if (state.overdueCount > 0) "${state.overdueCount} overdue" else "All on track"
    SectionCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Loans", style = MaterialTheme.typography.titleMedium, color = Ink)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(overdueColor)
                )
                Text(overdueNote, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = overdueColor)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LoanSummaryTile("YOU OWE", state.oweRemaining, "${state.oweCount} active", DangerText, DangerSurface, Modifier.weight(1f))
            LoanSummaryTile("YOU'RE OWED", state.lentRemaining, "${state.lentCount} active", PositiveText, AccentSurface, Modifier.weight(1f))
        }
    }
}

@Composable
private fun LoanSummaryTile(
    label: String,
    amount: Double,
    sub: String,
    labelColor: Color,
    background: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .padding(horizontal = 14.dp, vertical = 13.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = labelColor)
        Text(formatMoney(amount), fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = Ink, modifier = Modifier.padding(top = 3.dp))
        Text(sub, style = MaterialTheme.typography.labelMedium, color = InkFaint, modifier = Modifier.padding(top = 1.dp))
    }
}

@Composable
private fun TopCategoriesCard(state: HomeUiState, onSeeAll: () -> Unit) {
    val segments = buildList {
        state.topCategories.forEach { add(it.category.color to it.amount) }
        if (state.otherAmount > 0) add(OtherColor to state.otherAmount)
    }
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Top categories", style = MaterialTheme.typography.titleMedium, color = Ink)
            LinkText("All", onSeeAll)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryDonut(segments = segments, total = state.total)
            Spacer(Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                state.topCategories.forEach { LegendRow(it.category.color, it.category.label, formatMoney(it.amount)) }
                if (state.otherAmount > 0) {
                    LegendRow(OtherColor, "Other", formatMoney(state.otherAmount))
                }
            }
        }
    }
}

@Composable
private fun LegendRow(color: Color, name: String, amount: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = InkSoft,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(text = amount, style = MaterialTheme.typography.titleSmall, color = Ink)
    }
}

@Composable
private fun RisingCard(rising: List<Movement>, comparisonLabel: String) {
    SectionCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Rising this month", style = MaterialTheme.typography.titleMedium, color = Ink)
            Text(
                text = comparisonLabel,
                style = MaterialTheme.typography.labelMedium,
                color = DangerText,
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(DangerSurface)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
            rising.forEach { movement ->
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 7.dp)
                    ) {
                        CategoryBadge(movement.category, size = 27.dp, corner = 8.dp, fontSize = 10.5.sp)
                        Text(
                            text = movement.category.label,
                            style = MaterialTheme.typography.titleSmall,
                            color = Ink,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "+${movement.percent}%",
                            style = MaterialTheme.typography.titleSmall,
                            color = DangerText
                        )
                    }
                    ProgressBar(fraction = barFraction(movement, rising), color = movement.category.color)
                }
            }
        }
    }
}

private fun barFraction(movement: Movement, all: List<Movement>): Float {
    val max = all.maxOfOrNull { it.delta } ?: 1.0
    return if (max > 0) (movement.delta / max).toFloat() else 0f
}

@Composable
private fun ProgressBar(fraction: Float, color: Color, height: androidx.compose.ui.unit.Dp = 7.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(100.dp))
            .background(Track)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(100.dp))
                .background(color)
        )
    }
}

@Composable
private fun LinkText(text: String, onClick: () -> Unit) {
    Text(
        text = "$text ›",
        style = MaterialTheme.typography.labelLarge,
        color = InkFaint,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun EmptyState() {
    SectionCard(contentPadding = PaddingValues(vertical = 36.dp, horizontal = 18.dp)) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No expenses yet", style = MaterialTheme.typography.titleMedium, color = Ink)
            Spacer(Modifier.height(2.dp))
            Text(
                "Tap + to add your first one",
                style = MaterialTheme.typography.bodyMedium,
                color = InkMuted
            )
        }
    }
}
