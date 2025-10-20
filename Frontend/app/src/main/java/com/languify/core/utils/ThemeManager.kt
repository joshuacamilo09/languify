package com.languify.core.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a single instance of DataStore tied to the Context (extension property)
val Context.dataStore by preferencesDataStore("languify_settings")

object ThemeManager {

    // Key used to store the user's dark mode preference
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")

    /**
     * Saves the theme preference (true = dark mode)
     */
    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    /**
     * Reads the theme preference as a Flow (reactive updates)
     */
    fun getDarkMode(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            // Return false by default if no preference is saved yet
            preferences[DARK_MODE_KEY] ?: false
        }
    }
}
