package com.languify.infra.websocket.domain

import com.languify.infra.security.TokenStorage
import com.languify.infra.websocket.WebSocketClient
import com.languify.infra.websocket.WebSocketEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class WebSocketService(private val tokenStorage: TokenStorage) {
  private var client: WebSocketClient? = null

  val events: Flow<WebSocketEvent>
    get() = client?.events ?: emptyFlow()

  suspend fun connect() {
    disconnect()

    client = WebSocketClient(tokenStorage)
    client?.connect()
  }

  fun send(event: String, data: Any? = null) {
    client?.send(event, data) ?: throw IllegalStateException("WebSocket not connected")
  }

  fun disconnect() {
    client?.close()
    client = null
  }
}