package com.languify.infra.websocket

import com.google.gson.Gson
import com.languify.infra.security.TokenStorage
import com.languify.infra.websocket.domain.WebSocketMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import okhttp3.*

sealed class WebSocketEvent {
  data class OnOpen(val response: Response) : WebSocketEvent()

  data class OnMessage(val text: String) : WebSocketEvent()

  data class OnFailure(val throwable: Throwable, val response: Response?) : WebSocketEvent()

  data class OnClosing(val code: Int, val reason: String) : WebSocketEvent()

  data class OnClosed(val code: Int, val reason: String) : WebSocketEvent()
}

class WebSocketClient(
  private val tokenStorage: TokenStorage,
  private val url: String = "ws://10.0.2.2:8080/ws",
) {
  private var webSocket: WebSocket? = null
  private val eventChannel = Channel<WebSocketEvent>(Channel.BUFFERED)
  private val gson = Gson()

  val events: Flow<WebSocketEvent> = eventChannel.receiveAsFlow()

  suspend fun connect() {
    val token = tokenStorage.getToken()

    val request =
      Request.Builder()
        .url(url)
        .apply { if (token != null) addHeader("Authorization", "Bearer $token") }
        .build()

    val client = OkHttpClient.Builder().build()
    webSocket = client.newWebSocket(request, createWebSocketListener())
  }

  fun send(event: String, data: Any? = null) {
    val message = WebSocketMessage(event, data)
    val json = gson.toJson(message)
    webSocket?.send(json)
  }

  fun close() {
    webSocket?.close(1000, "Client closing")
    eventChannel.close()
  }

  private fun createWebSocketListener() =
    object : WebSocketListener() {
      override fun onOpen(webSocket: WebSocket, response: Response) {
        eventChannel.trySend(WebSocketEvent.OnOpen(response))
      }

      override fun onMessage(webSocket: WebSocket, text: String) {
        eventChannel.trySend(WebSocketEvent.OnMessage(text))
      }

      override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        eventChannel.trySend(WebSocketEvent.OnFailure(t, response))
      }

      override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        eventChannel.trySend(WebSocketEvent.OnClosing(code, reason))
      }

      override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        eventChannel.trySend(WebSocketEvent.OnClosed(code, reason))
      }
    }
}
