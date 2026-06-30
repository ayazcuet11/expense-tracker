package com.example.expensetracker.ui.screens.loans

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.LoanDirection
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.theme.Cream
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkFaint
import com.example.expensetracker.ui.theme.InkMuted
import com.example.expensetracker.ui.theme.OnInk
import com.example.expensetracker.ui.theme.PillBg
import com.example.expensetracker.ui.theme.PillBorder
import com.example.expensetracker.ui.theme.Surface as SurfaceColor
import com.example.expensetracker.util.formatDayLabel

@Composable
fun AddLoanScreen(
    onBack: () -> Unit,
    viewModel: AddLoanViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(Cream)) {
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
            Text("Add loan", style = MaterialTheme.typography.titleMedium, color = Ink)
            Spacer(Modifier.size(36.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Direction toggle.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PillBg)
                    .border(1.dp, PillBorder, RoundedCornerShape(16.dp))
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DirectionOption("I borrowed", state.direction == LoanDirection.BORROWED, Modifier.weight(1f)) {
                    viewModel.setDirection(LoanDirection.BORROWED)
                }
                DirectionOption("I lent", state.direction == LoanDirection.LENT, Modifier.weight(1f)) {
                    viewModel.setDirection(LoanDirection.LENT)
                }
            }

            FieldLabel(if (state.direction == LoanDirection.BORROWED) "Borrowed from" else "Lent to")
            TextInput(
                value = state.person,
                onValueChange = viewModel::setPerson,
                placeholder = "Name"
            )

            FieldLabel("Amount")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceColor)
                    .border(1.dp, PillBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("৳", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = InkFaint)
                BasicTextField(
                    value = state.amountText,
                    onValueChange = viewModel::setAmount,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Ink),
                    cursorBrush = SolidColor(Ink),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp, vertical = 14.dp),
                    decorationBox = { inner ->
                        if (state.amountText.isEmpty()) {
                            Text("0", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = InkFaint)
                        }
                        inner()
                    }
                )
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("Date borrowed", topPadding = 0.dp)
                    DateField(millis = state.borrowedDate, onPick = { it?.let(viewModel::setBorrowedDate) })
                }
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("Due date", topPadding = 0.dp)
                    DateField(millis = state.dueDate, onPick = viewModel::setDueDate, optional = true)
                }
            }

            FieldLabel("Note / reason")
            TextInput(
                value = state.note,
                onValueChange = viewModel::setNote,
                placeholder = "What was it for?"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PillBg)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (state.canSave) Ink else Ink.copy(alpha = 0.4f))
                    .clickable(enabled = state.canSave) { viewModel.save(onBack) },
                contentAlignment = Alignment.Center
            ) {
                Text("Save loan", style = MaterialTheme.typography.titleMedium, color = OnInk)
            }
        }
    }
}

@Composable
private fun DirectionOption(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) SurfaceColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
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
private fun FieldLabel(text: String, topPadding: androidx.compose.ui.unit.Dp = 18.dp) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = InkMuted,
        modifier = Modifier.padding(top = topPadding, bottom = 7.dp)
    )
}

@Composable
private fun TextInput(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Ink),
        cursorBrush = SolidColor(Ink),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceColor)
            .border(1.dp, PillBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 15.dp, vertical = 14.dp),
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(placeholder, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = InkFaint)
            }
            inner()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    millis: Long?,
    onPick: (Long?) -> Unit,
    optional: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceColor)
            .border(1.dp, PillBorder, RoundedCornerShape(14.dp))
            .clickable { showDialog = true }
            .padding(horizontal = 13.dp, vertical = 13.dp)
    ) {
        Text(
            text = millis?.let { formatDayLabel(it) } ?: if (optional) "Optional" else "Pick",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (millis == null) InkFaint else Ink
        )
    }

    if (showDialog) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = millis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onPick(pickerState.selectedDateMillis)
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
