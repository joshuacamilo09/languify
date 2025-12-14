package com.languify.infra.storage

import android.content.Context

object StorageProvider {
    private lateinit var secureStorage: SecureStorage
    private lateinit var tokenStorage: TokenStorage

    fun initialize(context: Context) {
        secureStorage = SecureStorage(context.applicationContext)
        tokenStorage = TokenStorage(secureStorage)
    }

    fun getTokenStorage(): TokenStorage {
        return tokenStorage
    }
}
