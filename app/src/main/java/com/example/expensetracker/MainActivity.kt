package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.prefs.ThemePreference
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.ThemeViewModel
import com.example.expensetracker.ui.navigation.ExpenseTrackerApp
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val theme by themeViewModel.theme.collectAsStateWithLifecycle()
            ExpenseTrackerTheme(dark = theme == ThemePreference.DARK) {
                ExpenseTrackerApp()
            }
        }
    }
}
