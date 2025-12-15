package com.languify.data.network

import android.util.Log
import okhttp3.*
import okio.ByteString

class RealtimeClient {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    var onMessageReceived: ((String) -> Unit)? = null

    fun connect() {
        val request = Request.Builder()
            .url("wss://convolutionary-teofila-claylike.ngrok-free.dev/ws/realtime")
            .build()

        val listener = object : WebSocketListener() {

            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d("Realtime", "✅ Conectado ao backend")
            }

            override fun onMessage(ws: WebSocket, text: String) {
                Log.d("RealtimeSpy", "📩 TEXTO (${text.length})")
                onMessageReceived?.invoke(text)
            }

            override fun onMessage(ws: WebSocket, bytes: ByteString) {
                Log.d("RealtimeSpy", "📩 BINÁRIO (${bytes.size})")
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e("Realtime", "❌ Erro: ${t.message}")
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(json: String) {
        webSocket?.send(json)
    }

    fun disconnect() {
        webSocket?.close(1000, "bye")
        webSocket = null
    }
}
