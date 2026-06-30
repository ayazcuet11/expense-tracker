package com.example.expensetracker.ui.screens.loans

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.LoanDirection
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.components.SectionCard
import com.example.expensetracker.ui.theme.AccentSurface
import com.example.expensetracker.ui.theme.Cream
import com.example.expensetracker.ui.theme.DangerSurface
import com.example.expensetracker.ui.theme.DangerText
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkFaint
import com.example.expensetracker.ui.theme.InkMuted
import com.example.expensetracker.ui.theme.OnInk
import com.example.expensetracker.ui.theme.PillBg
import com.example.expensetracker.ui.theme.PillBorder
import com.example.expensetracker.ui.theme.PositiveText
import com.example.expensetracker.ui.theme.Surface as SurfaceColor
import com.example.expensetracker.ui.theme.Track
import com.example.expensetracker.util.formatDayLabel
import com.example.expensetracker.util.formatMoney

/** Coral for "you owe", green for "you're owed". */
internal fun directionColor(direction: LoanDirection): Color =
    if (direction == LoanDirection.BORROWED) DangerText else PositiveText

@Composable
fun LoansScreen(
    onBack: () -> Unit,
    onAddLoan: () -> Unit,
    viewModel: LoansViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val paySheet by viewModel.paySheet.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        item { LoansHeader(onBack = onBack, onAddLoan = onAddLoan) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NetTile(
                    label = "YOU OWE",
                    amount = state.oweRemaining,
                    labelColor = DangerText,
                    background = DangerSurface,
                    modifier = Modifier.weight(1f)
                )
                NetTile(
                    label = "YOU'RE OWED",
                    amount = state.lentRemaining,
                    labelColor = PositiveText,
                    background = AccentSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            LoanTabs(
                tab = state.tab,
                oweCount = state.oweCount,
                lentCount = state.lentCount,
                onSelect = viewModel::setTab
            )
        }

        if (state.loans.isEmpty()) {
            item { EmptyLoans(state.tab) }
        } else {
            items(state.loans, key = { it.id }) { loan ->
                LoanCard(loan = loan, onPay = { viewModel.openPayment(loan.id) })
            }
        }
    }

    paySheet?.let { sheet ->
        RecordPaymentSheet(
            state = sheet,
            onAmountChange = viewModel::setPayAmount,
            onHalf = viewModel::payHalf,
            onFull = viewModel::payFull,
            onSave = viewModel::savePayment,
            onDismiss = viewModel::closePayment
        )
    }
}

@Composable
private fun LoansHeader(onBack: () -> Unit, onAddLoan: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(PillBg)
                    .border(1.dp, PillBorder, RoundedCornerShape(100.dp))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink, modifier = Modifier.size(18.dp))
            }
            Text("Loans", style = MaterialTheme.typography.headlineMedium, color = Ink)
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(Ink)
                .clickable(onClick = onAddLoan)
                .padding(start = 11.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = OnInk, modifier = Modifier.size(17.dp))
            Text("Add loan", style = MaterialTheme.typography.labelLarge, color = OnInk)
        }
    }
}

@Composable
private fun NetTile(
    label: String,
    amount: Double,
    labelColor: Color,
    background: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .padding(horizontal = 16.dp, vertical = 15.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = labelColor)
        Text(
            text = formatMoney(amount),
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = Ink,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun LoanTabs(
    tab: LoanDirection,
    oweCount: Int,
    lentCount: Int,
    onSelect: (LoanDirection) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(100.dp))
            .background(PillBg)
            .border(1.dp, PillBorder, RoundedCornerShape(100.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        LoanTab("I owe · $oweCount", tab == LoanDirection.BORROWED, Modifier.weight(1f)) {
            onSelect(LoanDirection.BORROWED)
        }
        LoanTab("Owed to me · $lentCount", tab == LoanDirection.LENT, Modifier.weight(1f)) {
            onSelect(LoanDirection.LENT)
        }
    }
}

@Composable
private fun LoanTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (selected) SurfaceColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (selected) Ink else InkMuted
        )
    }
}

@Composable
private fun LoanCard(loan: LoanCardUi, onPay: () -> Unit) {
    val dirColor = directionColor(loan.direction)
    val statusStyle = loan.status.style()
    SectionCard(cornerRadius = 22.dp, contentPadding = PaddingValues(17.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(dirColor),
                contentAlignment = Alignment.Center
            ) {
                Text(loan.initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(loan.person, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Ink)
                if (loan.note.isNotBlank()) {
                    Text(
                        loan.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = InkFaint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatMoney(loan.remaining), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = dirColor)
                Text("remaining", style = MaterialTheme.typography.labelSmall, color = InkFaint)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 8.dp)
                .height(7.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Track)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(loan.progress)
                    .height(7.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(dirColor)
            )
        }
        Text(
            text = "${formatMoney(loan.paid)} of ${formatMoney(loan.principal)} · ${loan.percentLabel}",
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = loan.status.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = statusStyle.text,
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(statusStyle.background)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
            Text("Since ${formatDayLabel(loan.borrowedDate)}", style = MaterialTheme.typography.labelMedium, color = InkFaint)
            Spacer(Modifier.weight(1f))
            if (loan.canPay) {
                Text(
                    text = "Record payment",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Ink,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Cream)
                        .border(1.dp, PillBorder, RoundedCornerShape(100.dp))
                        .clickable(onClick = onPay)
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyLoans(tab: LoanDirection) {
    SectionCard(contentPadding = PaddingValues(vertical = 36.dp, horizontal = 18.dp)) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (tab == LoanDirection.BORROWED) "Nothing borrowed" else "Nothing lent",
                style = MaterialTheme.typography.titleMedium,
                color = Ink
            )
            Spacer(Modifier.height(2.dp))
            Text("Tap “Add loan” to track one", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
        }
    }
}
