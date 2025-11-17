package com.languify.identity.auth.domain

import com.languify.infra.security.TokenStorage
import kotlinx.coroutines.flow.first

class AuthService(private val tokenStorage: TokenStorage) {
  suspend fun getToken(): String? {
    return tokenStorage.token.first()
  }

  suspend fun isAuthenticated(): Boolean {
    val token = tokenStorage.token.first()
    return token != null
  }

  suspend fun signOut() {
    tokenStorage.clearToken()
  }
}
