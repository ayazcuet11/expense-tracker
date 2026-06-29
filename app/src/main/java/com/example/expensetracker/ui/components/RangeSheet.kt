package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.screens.common.DateRange
import com.example.expensetracker.ui.theme.Cream
import com.example.expensetracker.ui.theme.Divider
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.OnInk

/** Bottom sheet to choose a [DateRange], styled to match the design's range picker. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeSheet(
    selected: DateRange,
    onSelect: (DateRange) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Cream,
        dragHandle = null
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 30.dp)) {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xFFDCD5C7))
            )
            Text(
                text = "Date range",
                style = MaterialTheme.typography.titleMedium,
                color = Ink,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            DateRange.entries.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(option) }
                        .padding(vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = option.sheetLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Ink
                    )
                    if (option == selected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Ink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = OnInk,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                if (option != DateRange.entries.last()) {
                    HorizontalDivider(color = Divider, thickness = 1.dp)
                }
            }
        }
    }
}
