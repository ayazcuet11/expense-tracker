package com.example.expensetracker.ui.screens.loans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Loan
import com.example.expensetracker.data.model.LoanDirection
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddLoanUiState(
    val direction: LoanDirection = LoanDirection.BORROWED,
    val person: String = "",
    val amountText: String = "",
    val borrowedDate: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val note: String = ""
) {
    val amount: Double get() = amountText.toDoubleOrNull() ?: 0.0
    val canSave: Boolean get() = amount > 0.0 && person.isNotBlank()
}

class AddLoanViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLoanUiState())
    val uiState: StateFlow<AddLoanUiState> = _uiState.asStateFlow()

    fun setDirection(direction: LoanDirection) = _uiState.update { it.copy(direction = direction) }

    fun setPerson(value: String) = _uiState.update { it.copy(person = value) }

    fun setAmount(value: String) = _uiState.update { it.copy(amountText = sanitizeAmount(value)) }

    fun setBorrowedDate(millis: Long) = _uiState.update { it.copy(borrowedDate = millis) }

    fun setDueDate(millis: Long?) = _uiState.update { it.copy(dueDate = millis) }

    fun setNote(value: String) = _uiState.update { it.copy(note = value) }

    /** Persists the loan and invokes [onDone] once written. */
    fun save(onDone: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            repository.addLoan(
                Loan(
                    person = state.person.trim(),
                    direction = state.direction,
                    principal = state.amount,
                    borrowedDate = state.borrowedDate,
                    dueDate = state.dueDate,
                    note = state.note.trim()
                )
            )
            onDone()
        }
    }

    private fun sanitizeAmount(text: String): String {
        val filtered = text.filterIndexed { index, c ->
            c.isDigit() || (c == '.' && text.indexOf('.') == index)
        }
        val dot = filtered.indexOf('.')
        return if (dot >= 0) filtered.substring(0, minOf(filtered.length, dot + 3)) else filtered
    }
}
