package com.example.expensetracker.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** The user-selectable color themes; the app never follows the system setting. */
enum class ThemePreference { LIGHT, DARK }

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/** Persists the chosen theme across app restarts. Built once in ExpenseApp, like the repository. */
class ThemePreferenceStore(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme")

    val theme: Flow<ThemePreference> = context.settingsDataStore.data.map { prefs ->
        prefs[themeKey]
            ?.let { stored -> ThemePreference.entries.firstOrNull { it.name == stored } }
            ?: ThemePreference.LIGHT
    }

    suspend fun setTheme(value: ThemePreference) {
        context.settingsDataStore.edit { it[themeKey] = value.name }
    }
}
