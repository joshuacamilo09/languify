package com.languify.infra.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(context: Context) {
    private val masterKey =
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private val preferences =
        EncryptedSharedPreferences.create(
            context,
            "languify_secure_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    fun putString(
        key: String,
        value: String,
    ) {
        preferences.edit().putString(key, value).apply()
    }

    fun removeString(key: String) {
        preferences.edit().remove(key).apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }
}
