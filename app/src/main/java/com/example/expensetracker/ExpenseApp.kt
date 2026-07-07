package com.example.expensetracker

import android.app.Application
import com.example.expensetracker.data.local.ExpenseDatabase
import com.example.expensetracker.data.prefs.ThemePreferenceStore
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Minimal manual DI: a single repository instance built lazily from the Room database and shared
 * across the app. ViewModels obtain it through [AppViewModelProvider].
 */
class ExpenseApp : Application() {

    val repository: ExpenseRepository by lazy {
        val db = ExpenseDatabase.getInstance(this)
        ExpenseRepository(db.expenseDao(), db.loanDao())
    }

    val themePreferences: ThemePreferenceStore by lazy { ThemePreferenceStore(this) }

    override fun onCreate() {
        super.onCreate()
        // Seed the demo data once, off the main thread.
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            repository.seedIfEmpty()
        }
    }
}
