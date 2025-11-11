package com.languify.core.localization

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

val Context.languageDataStore by preferencesDataStore("language_settings")

object LanguageManager {

    private val LANGUAGE_KEY = stringPreferencesKey("language_code")

    suspend fun setLanguage(context: Context, code: String) {
        context.languageDataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = code
        }
    }

    fun getLanguage(context: Context): Flow<String> {
        return context.languageDataStore.data.map { prefs ->
            prefs[LANGUAGE_KEY] ?: "en"
        }
    }
}
