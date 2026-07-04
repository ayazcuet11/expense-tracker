package com.example.expensetracker.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.components.RangePill
import com.example.expensetracker.ui.components.RangeSheet
import com.example.expensetracker.ui.components.SectionCard
import com.example.expensetracker.ui.components.TransactionItem
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkMuted
import com.example.expensetracker.util.formatSignedMoney

@Composable
fun ActivityScreen(
    onTransactionClick: (Long) -> Unit,
    viewModel: ActivityViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var sheetOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Activity", style = MaterialTheme.typography.headlineMedium, color = Ink)
                RangePill(label = state.rangePill, onClick = { sheetOpen = true })
            }
        }

        if (state.isEmpty) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 64.dp), contentAlignment = Alignment.Center) {
                    Text("No activity in this range", style = MaterialTheme.typography.bodyLarge, color = InkMuted)
                }
            }
        } else {
            state.groups.forEach { group ->
                item(key = "h-${group.dayLabel}") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 2.dp, end = 2.dp, top = 6.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(group.dayLabel, style = MaterialTheme.typography.labelLarge, color = InkMuted)
                        Text(formatSignedMoney(group.dayTotal), style = MaterialTheme.typography.labelLarge, color = InkMuted)
                    }
                }
                item(key = "c-${group.dayLabel}") {
                    SectionCard(
                        cornerRadius = 20.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        group.transactions.forEachIndexed { index, expense ->
                            TransactionItem(
                                expense = expense,
                                onClick = { onTransactionClick(expense.id) },
                                showDivider = index != group.transactions.lastIndex
                            )
                        }
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
