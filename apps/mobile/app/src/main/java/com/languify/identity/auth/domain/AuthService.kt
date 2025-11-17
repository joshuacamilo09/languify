package com.languify.identity.auth.domain

import com.languify.infra.security.TokenStorage
import com.languify.infra.websocket.domain.WebSocketService
import kotlinx.coroutines.flow.first

class AuthService(
  private val tokenStorage: TokenStorage,
  private val webSocketService: WebSocketService
) {
  suspend fun getToken(): String? {
    return tokenStorage.token.first()
  }

  suspend fun isAuthenticated(): Boolean {
    val token = tokenStorage.token.first()
    return token != null
  }

  suspend fun onSign() {
    webSocketService.connect()
  }

  suspend fun signOut() {
    webSocketService.disconnect()
    tokenStorage.clearToken()
  }
}
