package com.languify.infra.websocket

import com.languify.infra.security.TokenStorage
import kotlinx.coroutines.flow.Flow

class WebSocketService(private val tokenStorage: TokenStorage) {
  private var client: WebSocketClient? = null

  val events: Flow<WebSocketEvent>
    get() = client?.events ?: throw IllegalStateException("WebSocket not connected")

  suspend fun connect() {
    disconnect()
    
    client = WebSocketClient(tokenStorage)
    client?.connect()
  }

  fun send(message: String) {
    client?.send(message) ?: throw IllegalStateException("WebSocket not connected")
  }

  fun disconnect() {
    client?.close()
    client = null
  }
}
