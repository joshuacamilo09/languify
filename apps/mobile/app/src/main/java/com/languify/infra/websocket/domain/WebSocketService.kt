package com.languify.infra.websocket.domain

import com.languify.infra.security.TokenStorage
import com.languify.infra.websocket.WebSocketClient
import com.languify.infra.websocket.WebSocketEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WebSocketService(private val tokenStorage: TokenStorage) {
  private var client: WebSocketClient? = null
  private var reconnectJob: Job? = null
  private var shouldReconnect = false
  private var reconnectScope: CoroutineScope? = null

  val events: Flow<WebSocketEvent>
    get() = client?.events ?: emptyFlow()

  suspend fun connect(scope: CoroutineScope? = null, autoReconnect: Boolean = true) {
    shouldReconnect = autoReconnect
    reconnectScope = scope

    disconnect()

    try {
      client = WebSocketClient(tokenStorage)
      client?.connect()

      if (autoReconnect && scope != null) startReconnectMonitoring(scope)
    } catch (e: Exception) {
      if (autoReconnect && scope != null) scheduleReconnect(scope)
      else throw e
    }
  }

  private fun startReconnectMonitoring(scope: CoroutineScope) {
    reconnectJob = scope.launch {
      client?.events?.collect { event ->
        when (event) {
          is WebSocketEvent.OnFailure,
          is WebSocketEvent.OnClosed -> if (shouldReconnect && isActive) scheduleReconnect(scope)
          else -> {}
        }
      }
    }
  }

  private fun scheduleReconnect(scope: CoroutineScope, retryDelayMs: Long = 5000) {
    reconnectJob?.cancel()

    reconnectJob = scope.launch {
      while (shouldReconnect && isActive) {
        delay(retryDelayMs)

        try {
          client = WebSocketClient(tokenStorage)
          client?.connect()

          startReconnectMonitoring(scope)
          break
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
  }

  fun send(event: String, data: Any? = null) {
    client?.send(event, data) ?: throw IllegalStateException("WebSocket not connected")
  }

  fun disconnect() {
    shouldReconnect = false
    reconnectJob?.cancel()
    reconnectJob = null
    client?.close()
    client = null
  }
}