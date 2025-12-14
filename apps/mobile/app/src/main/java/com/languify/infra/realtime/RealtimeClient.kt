package com.languify.infra.realtime

import com.google.gson.Gson
import com.languify.infra.realtime.data.listener.RealtimeEventListener
import com.languify.infra.realtime.data.model.RealtimeEvent
import com.languify.infra.realtime.data.model.RealtimeMessage
import com.languify.infra.realtime.data.model.RealtimeState
import com.languify.infra.storage.StorageProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

object RealtimeClient {
    private const val WS_URL = "ws://10.0.2.2:8080/ws/conversation"
    private const val NORMAL_CLOSURE_STATUS = 1000

    private val gson = Gson()
    private val listeners = mutableListOf<RealtimeEventListener>()

    private var webSocket: WebSocket? = null
    private var currentState: RealtimeState = RealtimeState.Disconnected

    private val client =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    fun connect() {
        val token = StorageProvider.getTokenStorage().getToken()
        updateState(RealtimeState.Connecting)

        val request =
            Request.Builder()
                .url(WS_URL)
                .addHeader("Authorization", "Bearer $token")
                .build()

        webSocket =
            client.newWebSocket(
                request,
                object : WebSocketListener() {
                    override fun onOpen(
                        webSocket: WebSocket,
                        response: Response,
                    ) {
                        updateState(RealtimeState.Connected)
                    }

                    override fun onMessage(
                        webSocket: WebSocket,
                        text: String,
                    ) {
                        handleMessage(text)
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: Response?,
                    ) {
                        updateState(RealtimeState.Failed(t))
                    }

                    override fun onClosing(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String,
                    ) {
                        webSocket.close(NORMAL_CLOSURE_STATUS, null)
                        updateState(RealtimeState.Disconnected)
                    }

                    override fun onClosed(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String,
                    ) {
                        updateState(RealtimeState.Disconnected)
                    }
                },
            )
    }

    fun sendMessage(message: RealtimeMessage) {
        val text = gson.toJson(message)
        webSocket?.send(text)
    }

    fun addListener(listener: RealtimeEventListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: RealtimeEventListener) {
        listeners.remove(listener)
    }

    fun getState(): RealtimeState = currentState

    private fun updateState(newState: RealtimeState) {
        currentState = newState
        listeners.forEach { it.onStateChanged(newState) }
    }

    private fun handleMessage(text: String) {
        listeners.forEach { it.onRawMessage(text) }

        try {
            val message = gson.fromJson(text, RealtimeMessage::class.java)
            val event = RealtimeEvent.fromMessage(message)
            listeners.forEach { it.onEvent(event) }
        } catch (e: Exception) {
        }
    }
}
