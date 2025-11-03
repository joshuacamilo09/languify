package com.languify.core

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "languify_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
    }

    //LOGIN
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_LOGGED_IN] ?: false }

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { it[KEY_IS_LOGGED_IN] = value }
    }

    // DARK MODE
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_DARK_MODE] ?: false }

    suspend fun setDarkMode(value: Boolean) {
        context.dataStore.edit { it[KEY_IS_DARK_MODE] = value }
    }

    // LANGUAGE
    val language: Flow<String> = context.dataStore.data.map { it[KEY_LANGUAGE] ?: "en" }

    suspend fun setLanguage(value: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = value }
    }
}
