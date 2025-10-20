package com.languify.core.localization

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Reuse the DataStore instance pattern
val Context.languageDataStore by preferencesDataStore("language_settings")

object LanguageManager {

    private val LANGUAGE_KEY = stringPreferencesKey("app_language")

    /**
     * Saves the user's selected language (e.g., "en", "pt", "fr", etc.)
     */
    suspend fun setLanguage(context: Context, langCode: String) {
        context.languageDataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = langCode
        }
    }

    /**
     * Reads the saved language as a Flow (defaults to English)
     */
    fun getLanguage(context: Context): Flow<String> {
        return context.languageDataStore.data.map { prefs ->
            prefs[LANGUAGE_KEY] ?: "en"
        }
    }
}
