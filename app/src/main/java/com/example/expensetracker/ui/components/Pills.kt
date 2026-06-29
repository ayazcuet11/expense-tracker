package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkMuted
import com.example.expensetracker.ui.theme.PillBg
import com.example.expensetracker.ui.theme.PillBorder

/** Rounded date-range selector pill: a label and a chevron. */
@Composable
fun RangePill(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(PillBg)
            .border(BorderStroke(1.dp, PillBorder), RoundedCornerShape(100.dp))
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Ink
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = InkMuted,
            modifier = Modifier.padding(0.dp)
        )
    }
}

/** Small percentage-delta chip used under totals. Colors are supplied by the caller. */
@Composable
fun DeltaChip(
    up: Boolean,
    percent: Int,
    textColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${if (up) "▲" else "▼"} $percent%",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
