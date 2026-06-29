package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.theme.Accent
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.OnInkMuted
import com.example.expensetracker.ui.theme.PillBorder
import com.example.expensetracker.ui.theme.Surface as SurfaceColor

data class NavTab(val route: String, val label: String, val icon: ImageVector)

val bottomTabs = listOf(
    NavTab("home", "Home", Icons.Outlined.Home),
    NavTab("activity", "Activity", Icons.AutoMirrored.Outlined.List),
    NavTab("stats", "Stats", Icons.Outlined.BarChart),
    NavTab("insights", "Insights", Icons.Outlined.AutoAwesome)
)

/** Custom bottom bar: four tabs split around a floating green "add" button. */
@Composable
fun BottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth().height(84.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceColor)
        )
        // Hairline top border.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(PillBorder)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            TabGroup(bottomTabs.take(2), currentRoute, onNavigate, Modifier.weight(1f))
            Spacer(Modifier.width(66.dp))
            TabGroup(bottomTabs.drop(2), currentRoute, onNavigate, Modifier.weight(1f))
        }

        // Floating add button straddling the top edge.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(58.dp)
                .shadow(14.dp, RoundedCornerShape(20.dp), clip = false)
                .clip(RoundedCornerShape(20.dp))
                .background(Accent)
                .clickable(onClick = onAdd),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add expense", tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun TabGroup(
    tabs: List<NavTab>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            val color = if (selected) Ink else OnInkMuted
            val interaction = remember { MutableInteractionSource() }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = interaction,
                    indication = null
                ) { onNavigate(tab.route) }
            ) {
                Icon(tab.icon, contentDescription = tab.label, tint = color, modifier = Modifier.size(24.dp))
                Spacer(Modifier.height(4.dp))
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 10.sp,
                    color = color
                )
            }
        }
    }
}
