package com.example.expensetracker.ui.screens.insights

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.components.CategoryBadge
import com.example.expensetracker.ui.components.SectionCard
import com.example.expensetracker.ui.theme.Accent
import com.example.expensetracker.ui.theme.DangerSurface
import com.example.expensetracker.ui.theme.DangerText
import com.example.expensetracker.ui.theme.Divider
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkFaint
import com.example.expensetracker.ui.theme.InkMuted
import com.example.expensetracker.ui.theme.InkSoft
import com.example.expensetracker.ui.theme.Outline
import com.example.expensetracker.ui.theme.Surface as SurfaceColor

private val Amber = Color(0xFFB58A3C)

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text("Insights", style = MaterialTheme.typography.headlineMedium, color = Ink)
                Text(
                    "For your money — and your family",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }

        if (state.adjust.isNotEmpty()) {
            item { SectionLabel("Adjust your spending", DangerText) }
            items(state.adjust) { tip ->
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = SurfaceColor,
                    border = BorderStroke(1.dp, Outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(DangerText)
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(11.dp),
                                modifier = Modifier.padding(bottom = 9.dp)
                            ) {
                                CategoryBadge(tip.category, size = 32.dp, corner = 10.dp)
                                Text(
                                    tip.category.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Ink,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    tip.stat,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DangerText,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(DangerSurface)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(tip.tip, style = MaterialTheme.typography.bodyMedium, color = InkSoft)
                        }
                    }
                }
            }
        }

        if (state.add.isNotEmpty()) {
            item { SectionLabel("Worth adding", Accent) }
            items(state.add) { idea ->
                SectionCard(cornerRadius = 22.dp, contentPadding = PaddingValues(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.dp),
                        modifier = Modifier.padding(bottom = 9.dp)
                    ) {
                        MonoBadge(idea.mono, idea.color)
                        Column {
                            Text(idea.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Ink)
                            Text(idea.subtitle, style = MaterialTheme.typography.labelMedium, color = InkFaint)
                        }
                    }
                    Text(idea.tip, style = MaterialTheme.typography.bodyMedium, color = InkSoft)
                    Spacer(Modifier.height(11.dp))
                    Text(
                        idea.cta,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Accent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFF0F4F0))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (state.trim.isNotEmpty()) {
            item { SectionLabel("Consider trimming", Amber) }
            item {
                SectionCard(cornerRadius = 22.dp, contentPadding = PaddingValues(horizontal = 17.dp)) {
                    state.trim.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.padding(vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CategoryBadge(item.category, size = 30.dp, corner = 9.dp)
                            Column {
                                Text(item.line, style = MaterialTheme.typography.titleSmall, color = Ink)
                                Text(item.sub, style = MaterialTheme.typography.bodySmall, color = InkFaint)
                            }
                        }
                        if (index != state.trim.lastIndex) HorizontalDivider(color = Divider, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, dotColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 14.dp, bottom = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(dotColor)
        )
        Text(text, style = MaterialTheme.typography.titleMedium, color = Ink)
    }
}

@Composable
private fun MonoBadge(mono: String, color: Color) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(mono, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
    }
}
