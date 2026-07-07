package com.example.expensetracker.ui.screens.loans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Loan
import com.example.expensetracker.data.model.LoanDirection
import com.example.expensetracker.data.model.LoanStatus
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** A single loan ready to render — amounts stay raw; the screen formats them. */
data class LoanCardUi(
    val id: Long,
    val person: String,
    val initial: String,
    val note: String,
    val direction: LoanDirection,
    val principal: Double,
    val paid: Double,
    val remaining: Double,
    val progress: Float,
    val percentLabel: String,
    val status: LoanStatus,
    val borrowedDate: Long,
    val canPay: Boolean
)

data class LoansUiState(
    val tab: LoanDirection = LoanDirection.BORROWED,
    val oweRemaining: Double = 0.0,
    val lentRemaining: Double = 0.0,
    val oweCount: Int = 0,
    val lentCount: Int = 0,
    val loans: List<LoanCardUi> = emptyList()
)

/** In-progress "record payment" sheet, or null when closed. */
data class PaySheetState(
    val loanId: Long,
    val person: String,
    val direction: LoanDirection,
    val remaining: Double,
    val amountText: String = ""
) {
    val amount: Double get() = amountText.toDoubleOrNull() ?: 0.0
    val canSave: Boolean get() = amount > 0.0
}

class LoansViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val tab = MutableStateFlow(LoanDirection.BORROWED)

    val uiState: StateFlow<LoansUiState> =
        combine(repository.loans, tab) { loans, selectedTab ->
            val now = System.currentTimeMillis()
            val active = loans.filterNot { it.isSettled }
            val (owe, lent) = active.partition { it.direction == LoanDirection.BORROWED }

            val cards = loans
                .filter { it.direction == selectedTab }
                // Active loans first, then settled; each newest-borrowed first.
                .sortedWith(compareBy({ it.isSettled }, { -it.borrowedDate }))
                .map { it.toCardUi(now) }

            LoansUiState(
                tab = selectedTab,
                oweRemaining = owe.sumOf { it.remaining },
                lentRemaining = lent.sumOf { it.remaining },
                oweCount = owe.size,
                lentCount = lent.size,
                loans = cards
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LoansUiState()
        )

    private val _paySheet = MutableStateFlow<PaySheetState?>(null)
    val paySheet: StateFlow<PaySheetState?> = _paySheet.asStateFlow()

    fun setTab(direction: LoanDirection) {
        tab.value = direction
    }

    fun openPayment(loanId: Long) {
        viewModelScope.launch {
            repository.getLoan(loanId)?.let { loan ->
                _paySheet.value = PaySheetState(
                    loanId = loan.id,
                    person = loan.person,
                    direction = loan.direction,
                    remaining = loan.remaining
                )
            }
        }
    }

    fun closePayment() {
        _paySheet.value = null
    }

    fun setPayAmount(text: String) {
        _paySheet.update { it?.copy(amountText = sanitizeAmount(text)) }
    }

    fun payHalf() {
        _paySheet.update { it?.copy(amountText = trimAmount(it.remaining / 2.0)) }
    }

    fun payFull() {
        _paySheet.update { it?.copy(amountText = trimAmount(it.remaining)) }
    }

    fun savePayment() {
        val sheet = _paySheet.value ?: return
        if (!sheet.canSave) return
        viewModelScope.launch {
            repository.recordPayment(sheet.loanId, sheet.amount)
            _paySheet.value = null
        }
    }

    /** Loan awaiting delete confirmation, or null when no dialog is showing. */
    private val _pendingDelete = MutableStateFlow<LoanCardUi?>(null)
    val pendingDelete: StateFlow<LoanCardUi?> = _pendingDelete.asStateFlow()

    fun requestDelete(loan: LoanCardUi) {
        _pendingDelete.value = loan
    }

    fun cancelDelete() {
        _pendingDelete.value = null
    }

    fun confirmDelete() {
        val card = _pendingDelete.value ?: return
        viewModelScope.launch {
            repository.getLoan(card.id)?.let { repository.deleteLoan(it) }
            _pendingDelete.value = null
        }
    }

    private fun Loan.toCardUi(now: Long): LoanCardUi = LoanCardUi(
        id = id,
        person = person,
        initial = person.trim().firstOrNull()?.uppercase() ?: "?",
        note = note,
        direction = direction,
        principal = principal,
        paid = paidAmount,
        remaining = remaining,
        progress = progress,
        percentLabel = "${(progress * 100).toInt()}%",
        status = status(now),
        borrowedDate = borrowedDate,
        canPay = !isSettled
    )

    /** Keep only digits and a single decimal point, max two decimals. */
    private fun sanitizeAmount(text: String): String {
        val filtered = text.filterIndexed { index, c ->
            c.isDigit() || (c == '.' && text.indexOf('.') == index)
        }
        val dot = filtered.indexOf('.')
        return if (dot >= 0) filtered.substring(0, minOf(filtered.length, dot + 3)) else filtered
    }

    private fun trimAmount(amount: Double): String =
        if (amount % 1.0 == 0.0) amount.toLong().toString() else "%.2f".format(amount)
}
