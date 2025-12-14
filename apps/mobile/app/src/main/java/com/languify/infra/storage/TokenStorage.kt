package com.languify.infra.storage

class TokenStorage(private val secureStorage: SecureStorage) {
    companion object {
        private const val SESSION_TOKEN = "session_token"
    }

    fun getToken(): String? {
        return secureStorage.getString(SESSION_TOKEN)
    }

    fun putToken(token: String) {
        secureStorage.putString(SESSION_TOKEN, token)
    }

    fun removeToken() {
        secureStorage.removeString(SESSION_TOKEN)
    }
}
