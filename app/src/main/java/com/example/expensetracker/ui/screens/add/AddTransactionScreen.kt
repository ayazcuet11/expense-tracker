package com.example.expensetracker.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.theme.Accent
import com.example.expensetracker.ui.theme.Cream
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkMuted
import com.example.expensetracker.ui.theme.PillBg
import com.example.expensetracker.ui.theme.PillBorder
import com.example.expensetracker.ui.theme.Surface as SurfaceColor

@Composable
fun AddTransactionScreen(
    onBack: () -> Unit,
    viewModel: AddTransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        // Top bar.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(PillBg)
                    .border(1.dp, PillBorder, RoundedCornerShape(100.dp))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Ink, modifier = Modifier.size(19.dp))
            }
            Text(
                text = if (state.isEditing) "Edit expense" else "Add expense",
                style = MaterialTheme.typography.titleMedium,
                color = Ink
            )
            Spacer(Modifier.size(36.dp))
        }

        // Amount display + selected category chip.
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.displayAmount,
                fontSize = 52.sp,
                fontWeight = FontWeight.SemiBold,
                color = Ink,
                letterSpacing = (-1).sp
            )
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(PillBg)
                    .padding(horizontal = 13.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(state.category.color)
                )
                Text(state.category.label, style = MaterialTheme.typography.labelLarge, color = Ink)
            }
        }

        // Category grid.
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
        ) {
            items(state.categories, key = { it.name }) { category ->
                CategoryTile(
                    category = category,
                    selected = category == state.category,
                    onClick = { viewModel.onCategoryChange(category) }
                )
            }
        }

        // Keypad + save.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PillBg)
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(".", "0", "del")
            )
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { key ->
                        KeypadKey(
                            key = key,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                when (key) {
                                    "." -> viewModel.pressDot()
                                    "del" -> viewModel.backspace()
                                    else -> viewModel.pressDigit(key)
                                }
                            }
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(top = 2.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (state.canSave) Accent else Accent.copy(alpha = 0.45f))
                    .clickable(enabled = state.canSave) { viewModel.save(onBack) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state.isEditing) "Save changes" else "Add expense",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CategoryTile(category: Category, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(category.color),
                contentAlignment = Alignment.Center
            ) {
                Text(category.mono, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(19.dp))
                        .border(2.5.dp, Ink, RoundedCornerShape(19.dp))
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = category.short,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkMuted,
            textAlign = TextAlign.Center,
            lineHeight = 11.sp
        )
    }
}

@Composable
private fun KeypadKey(key: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (key == "del") {
            Icon(Icons.Filled.Backspace, contentDescription = "Delete", tint = Ink, modifier = Modifier.size(22.dp))
        } else {
            Text(key, fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = Ink)
        }
    }
}
