package com.languify.data.repository

import com.languify.data.model.*
import com.languify.data.network.AuthController
import retrofit2.Response

class ChatRepository(
    private val api: AuthController.ApiService = AuthController.api
) {

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
}
