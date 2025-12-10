package com.languify.core

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    // escrever valores
    suspend fun setToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun setUserId(id: Long) {
        context.dataStore.edit { it[USER_ID_KEY] = id }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = lang }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { it[LOGGED_IN_KEY] = isLoggedIn }
    }

    //ler fluxos
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE_KEY] ?: false }
    val language: Flow<String> = context.dataStore.data.map { it[LANGUAGE_KEY] ?: "en" }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[LOGGED_IN_KEY] ?: false }
    val userId: Flow<Long> = context.dataStore.data.map { it[USER_ID_KEY] ?: -1L }

    // 🔹 Métodos diretos (para chamadas imediatas)
    suspend fun getToken(): String? = context.dataStore.data.map { it[TOKEN_KEY] }.first()
    suspend fun getUserId(): Long = context.dataStore.data.map { it[USER_ID_KEY] ?: -1L }.first()

    // 🔹 Limpar tudo
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
