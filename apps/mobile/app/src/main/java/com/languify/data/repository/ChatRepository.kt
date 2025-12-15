package com.languify.data.repository

import com.languify.data.model.*
import com.languify.data.network.AuthController
import com.languify.data.network.RealtimeClient
import retrofit2.Response

class ChatRepository(
    private val api: AuthController.ApiService = AuthController.api,
    // injetamos o cliente WebSocket aqui (com valor default para não quebrar código antigo)
    private val realtimeClient: RealtimeClient = RealtimeClient()
) {

    // PARTE REST

    suspend fun createChat(request: createChatRequst): Response<createChatResponse> =
        api.createChat(request)

    suspend fun getChats(userId: Long): Response<List<getChatsResponse>> =
        api.getChatsUser(userId)

    suspend fun searchMessage(chatId: Long, message: String): Response<getMessageResponse> =
        api.searchMessageInChat(chatId, message)

    suspend fun sendMessage(request: SendMessageDTO): Response<SendMessageDTO> =
        api.sendMessage(request)

    suspend fun deleteChat(chatId: Long, userId: Long): Response<DeleteChatResponse> =
        api.deleteChat(chatId, userId)


    // PARTE REALTIME

    // conectar e definir o que acontece quando chega uma mensagem
    fun connectToRealtime(onMessageReceived: (String) -> Unit) {
        // passamos a callback do ViewModel para o cliente
        realtimeClient.onMessageReceived = onMessageReceived
        realtimeClient.connect()
    }

    // enviar mensagem para o GPT-4o
    fun sendRealtimeEvent(jsonMessage: String) {
        realtimeClient.sendMessage(jsonMessage)
    }

    // desligar
    fun disconnectRealtime() {
        realtimeClient.disconnect()
    }
}