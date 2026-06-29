package com.example.expensetracker.ui.screens.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddUiState(
    val amountText: String = "",
    val category: Category = Category.GROCERY,
    val title: String = "",
    val date: Long = System.currentTimeMillis(),
    val isEditing: Boolean = false
) {
    val amount: Double get() = amountText.toDoubleOrNull() ?: 0.0
    val canSave: Boolean get() = amount > 0.0
    val displayAmount: String get() = "$" + (amountText.ifEmpty { "0" })
    val categories: List<Category> get() = Category.entries
}

class AddTransactionViewModel(
    private val repository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val editingId: Long? = savedStateHandle.get<String>("id")?.toLongOrNull()

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    init {
        editingId?.let { id ->
            viewModelScope.launch {
                repository.getById(id)?.let { e ->
                    _uiState.value = AddUiState(
                        amountText = trimAmount(e.amount),
                        category = e.category,
                        title = e.title,
                        date = e.date,
                        isEditing = true
                    )
                }
            }
        }
    }

    fun pressDigit(digit: String) = _uiState.update { state ->
        var text = state.amountText
        // Cap to two decimals and a sensible length.
        val decimals = text.substringAfter('.', "")
        if (text.contains('.') && decimals.length >= 2) return@update state
        text = if (text == "0") digit else text + digit
        if (text.replace(".", "").length > 9) return@update state
        state.copy(amountText = text)
    }

    fun pressDot() = _uiState.update { state ->
        val text = state.amountText
        when {
            text.contains('.') -> state
            text.isEmpty() -> state.copy(amountText = "0.")
            else -> state.copy(amountText = "$text.")
        }
    }

    fun backspace() = _uiState.update { it.copy(amountText = it.amountText.dropLast(1)) }

    fun onCategoryChange(category: Category) = _uiState.update { it.copy(category = category) }

    /** Persists the transaction and invokes [onDone] once written. */
    fun save(onDone: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            val expense = Expense(
                id = editingId ?: 0,
                title = state.title.ifBlank { "Added expense" },
                amount = state.amount,
                category = state.category,
                type = TransactionType.EXPENSE,
                date = state.date
            )
            if (state.isEditing) repository.update(expense) else repository.add(expense)
            onDone()
        }
    }

    private fun trimAmount(amount: Double): String =
        if (amount % 1.0 == 0.0) amount.toLong().toString() else amount.toString()
}
