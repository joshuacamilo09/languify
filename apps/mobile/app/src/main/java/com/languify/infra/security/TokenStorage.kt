package com.languify.infra.security

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class TokenStorage(private val context: Context) {
  private val tokenKey = stringPreferencesKey("session_token")
  val token: Flow<String?> = context.dataStore.data.map { preferences -> preferences[tokenKey] }

  suspend fun getToken(): String? {
    var res: String? = null
    context.dataStore.data.collect { preferences -> res = preferences[tokenKey] }
    return res
  }

  suspend fun saveToken(token: String) {
    context.dataStore.edit { preferences -> preferences[tokenKey] = token }
  }

  suspend fun clearToken() {
    context.dataStore.edit { preferences -> preferences.remove(tokenKey) }
  }
}
