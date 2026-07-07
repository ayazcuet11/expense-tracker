package com.example.expensetracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkFaint
import com.example.expensetracker.ui.theme.Track
import com.example.expensetracker.util.formatMoney

/** A donut with a category-colored ring and the total spent in the hole, as on Home. */
@Composable
fun CategoryDonut(
    segments: List<Pair<Color, Double>>,
    total: Double,
    modifier: Modifier = Modifier,
    diameter: Dp = 128.dp
) {
    // Resolved here: theme colors can't be read inside the draw lambda.
    val trackColor = Track
    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val strokeWidth = 18.dp.toPx()
            val inset = strokeWidth / 2
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            // Track ring.
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
            if (total > 0) {
                var startAngle = -90f
                segments.forEach { (color, value) ->
                    val sweep = (value / total).toFloat() * 360f
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    startAngle += sweep
                }
            }
        }
        androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TOTAL",
                color = InkFaint,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 0.8.sp,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = formatMoney(total),
                color = Ink,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )
        }
    }
}
