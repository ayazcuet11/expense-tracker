package com.example.expensetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.prefs.ThemePreference
import com.example.expensetracker.data.prefs.ThemePreferenceStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Exposes the persisted theme choice; activity-scoped so the whole app shares one instance. */
class ThemeViewModel(private val store: ThemePreferenceStore) : ViewModel() {

    val theme: StateFlow<ThemePreference> = store.theme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ThemePreference.LIGHT
    )

    fun setTheme(value: ThemePreference) {
        viewModelScope.launch { store.setTheme(value) }
    }
}
