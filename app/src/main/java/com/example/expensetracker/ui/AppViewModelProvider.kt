package com.example.expensetracker.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import com.example.expensetracker.ExpenseApp
import com.example.expensetracker.ui.screens.activity.ActivityViewModel
import com.example.expensetracker.ui.screens.add.AddTransactionViewModel
import com.example.expensetracker.ui.screens.home.HomeViewModel
import com.example.expensetracker.ui.screens.insights.InsightsViewModel
import com.example.expensetracker.ui.screens.loans.AddLoanViewModel
import com.example.expensetracker.ui.screens.loans.LoansViewModel
import com.example.expensetracker.ui.screens.stats.StatsViewModel

/** Factory wiring each ViewModel to the application-scoped repository. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { HomeViewModel(app().repository) }
        initializer { StatsViewModel(app().repository) }
        initializer { ActivityViewModel(app().repository) }
        initializer { InsightsViewModel(app().repository) }
        initializer { AddTransactionViewModel(app().repository, createSavedStateHandle()) }
        initializer { LoansViewModel(app().repository) }
        initializer { AddLoanViewModel(app().repository) }
        initializer { ThemeViewModel(app().themePreferences) }
    }
}

private fun CreationExtras.app(): ExpenseApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ExpenseApp
