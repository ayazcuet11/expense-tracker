package com.example.expensetracker.ui.screens.loans

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.model.LoanStatus
import com.example.expensetracker.ui.theme.Accent
import com.example.expensetracker.ui.theme.AccentSurface
import com.example.expensetracker.ui.theme.Amber
import com.example.expensetracker.ui.theme.AmberSurface
import com.example.expensetracker.ui.theme.DangerSurface
import com.example.expensetracker.ui.theme.DangerText
import com.example.expensetracker.ui.theme.InkMuted
import com.example.expensetracker.ui.theme.PillBg

/** Badge text + background colors for a loan status, shared by the Loans and Insights screens. */
data class LoanStatusStyle(val text: Color, val background: Color)

fun LoanStatus.style(): LoanStatusStyle = when (this) {
    LoanStatus.ACTIVE -> LoanStatusStyle(Accent, AccentSurface)
    LoanStatus.DUE_SOON -> LoanStatusStyle(Amber, AmberSurface)
    LoanStatus.OVERDUE -> LoanStatusStyle(DangerText, DangerSurface)
    LoanStatus.SETTLED -> LoanStatusStyle(InkMuted, PillBg)
}
