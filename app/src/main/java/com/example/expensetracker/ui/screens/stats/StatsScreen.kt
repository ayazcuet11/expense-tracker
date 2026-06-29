package com.example.expensetracker.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.components.CategoryBadge
import com.example.expensetracker.ui.components.RangeSheet
import com.example.expensetracker.ui.components.SectionCard
import com.example.expensetracker.ui.screens.common.CategoryStat
import com.example.expensetracker.ui.screens.common.Movement
import com.example.expensetracker.ui.theme.DangerText
import com.example.expensetracker.ui.theme.Divider
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkFaint
import com.example.expensetracker.ui.theme.OnInk
import com.example.expensetracker.ui.theme.OnInkMuted
import com.example.expensetracker.ui.theme.PillBg
import com.example.expensetracker.ui.theme.PillBorder
import com.example.expensetracker.ui.theme.PositiveText
import com.example.expensetracker.ui.theme.Track
import com.example.expensetracker.util.formatMoney

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var sheetOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(PillBg)
                    .border(1.dp, PillBorder, RoundedCornerShape(14.dp))
                    .clickable { sheetOpen = true }
                    .padding(horizontal = 15.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(state.rangeLabel, style = MaterialTheme.typography.labelLarge, color = Ink)
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = Ink)
            }
        }

        item { TotalCard(state) }

        if (state.breakdown.isNotEmpty()) {
            item { SectionHeader("Breakdown") }
            item {
                SectionCard(contentPadding = PaddingValues(horizontal = 18.dp)) {
                    state.breakdown.forEachIndexed { index, slice ->
                        BreakdownRow(slice, showDivider = index != state.breakdown.lastIndex)
                    }
                }
            }
        }

        if (state.rising.isNotEmpty() || state.falling.isNotEmpty()) {
            item { SectionHeader("Compared to last month") }
            item {
                SectionCard(contentPadding = PaddingValues(horizontal = 18.dp)) {
                    val movements = state.rising + state.falling
                    movements.forEachIndexed { index, movement ->
                        MovementRow(movement, showDivider = index != movements.lastIndex)
                    }
                }
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
}

@Composable
private fun TotalCard(state: StatsUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Ink)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "TOTAL · ${state.rangeLabel}",
                style = MaterialTheme.typography.labelSmall,
                color = OnInkMuted
            )
            Text(
                text = formatMoney(state.total),
                style = MaterialTheme.typography.displayMedium,
                color = OnInk,
                modifier = Modifier.padding(top = 6.dp)
            )
            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${if (state.deltaUp) "▲" else "▼"} ${state.deltaPercent}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (state.deltaUp) Color(0xFFF0A595) else Color(0xFF9AD3B4),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                )
                Text(
                    text = state.comparisonLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnInkMuted
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(slice: CategoryStat, showDivider: Boolean) {
    Column(modifier = Modifier.padding(vertical = 13.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            CategoryBadge(slice.category, size = 30.dp, corner = 9.dp)
            Text(
                text = slice.category.label,
                style = MaterialTheme.typography.titleSmall,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(formatMoney(slice.amount), style = MaterialTheme.typography.titleSmall, color = Ink)
            Text(
                text = "${slice.percent}%",
                style = MaterialTheme.typography.bodySmall,
                color = InkFaint,
                modifier = Modifier.width(34.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
        StatBar(fraction = slice.fractionOfMax, color = slice.category.color)
        if (showDivider) {
            Spacer(Modifier.height(13.dp))
            HorizontalDivider(color = Divider, thickness = 1.dp)
        }
    }
}

@Composable
private fun MovementRow(movement: Movement, showDivider: Boolean) {
    Column {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            CategoryBadge(movement.category, size = 30.dp, corner = 9.dp)
            Text(
                text = movement.category.label,
                style = MaterialTheme.typography.titleSmall,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = movement.deltaLabel,
                style = MaterialTheme.typography.bodySmall,
                color = InkFaint
            )
            Text(
                text = "${if (movement.rising) "▲" else "▼"} ${movement.percent}%",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = if (movement.rising) DangerText else PositiveText,
                modifier = Modifier.width(62.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
        if (showDivider) HorizontalDivider(color = Divider, thickness = 1.dp)
    }
}

@Composable
private fun StatBar(fraction: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(Track)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(6.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(color)
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = Ink,
        modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
    )
}
