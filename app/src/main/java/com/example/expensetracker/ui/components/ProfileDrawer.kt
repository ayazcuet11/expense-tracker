package com.example.expensetracker.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.prefs.ThemePreference
import com.example.expensetracker.ui.theme.Accent
import com.example.expensetracker.ui.theme.AppColors
import com.example.expensetracker.ui.theme.Cream
import com.example.expensetracker.ui.theme.DarkAppColors
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkFaint
import com.example.expensetracker.ui.theme.InkMuted
import com.example.expensetracker.ui.theme.LightAppColors
import com.example.expensetracker.ui.theme.OnInk
import com.example.expensetracker.ui.theme.PillBorder

/** Display name shown in the profile drawer; the avatar initial derives from it. */
const val UserDisplayName = "Ayaz"

/**
 * Profile panel sliding in from the right edge over a click-to-dismiss scrim. A custom overlay
 * rather than ModalNavigationDrawer, which only opens from the start (left) edge.
 */
@Composable
fun ProfileDrawer(
    open: Boolean,
    activeTheme: ThemePreference,
    onSelectTheme: (ThemePreference) -> Unit,
    onDismiss: () -> Unit
) {
    if (open) {
        BackHandler(onBack = onDismiss)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = open, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
        }
        AnimatedVisibility(
            visible = open,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .background(Cream)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(Ink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = UserDisplayName.first().uppercase(),
                            color = OnInk,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(UserDisplayName, style = MaterialTheme.typography.titleMedium, color = Ink)
                        Text("Personal account", style = MaterialTheme.typography.bodySmall, color = InkMuted)
                    }
                }

                Spacer(Modifier.height(28.dp))
                Text("COLOR THEME", style = MaterialTheme.typography.labelSmall, color = InkFaint)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ThemePreviewCard(
                        name = "Light",
                        palette = LightAppColors,
                        active = activeTheme == ThemePreference.LIGHT,
                        onClick = { onSelectTheme(ThemePreference.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemePreviewCard(
                        name = "Dark",
                        palette = DarkAppColors,
                        active = activeTheme == ThemePreference.DARK,
                        onClick = { onSelectTheme(ThemePreference.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/** A mini mock of the given palette (its own colors, not the active theme's) with a check badge. */
@Composable
private fun ThemePreviewCard(
    name: String,
    palette: AppColors,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (active) 2.dp else 1.dp,
                color = if (active) Accent else PillBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.cream)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.72f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.surface)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(palette.ink)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(palette.accent)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(palette.inkFaint)
                    )
                }
            }
            if (active) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(18.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "$name theme active",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = Ink,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 6.dp, bottom = 2.dp)
        )
    }
}
