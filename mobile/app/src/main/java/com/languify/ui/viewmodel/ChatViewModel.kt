package com.languify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.data.model.SendMessageDTO
import com.languify.data.model.createChatRequst
import com.languify.domain.usecase.*
import com.languify.domain.usecase.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val createChatUseCase: CreateChatUseCase,
    private val getChatsUseCase: GetChatsUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val deleteChatUseCase: DeleteChatUseCase
) : ViewModel() {

    private val _createChatState = MutableStateFlow<Result<com.languify.data.model.createChatResponse>>(Result.Loading)
    val createChatState: StateFlow<Result<com.languify.data.model.createChatResponse>> = _createChatState

    private val _chatsState = MutableStateFlow<Result<List<com.languify.data.model.getChatsResponse>>>(Result.Loading)
    val chatsState: StateFlow<Result<List<com.languify.data.model.getChatsResponse>>> = _chatsState

    private val _sendMessageState = MutableStateFlow<Result<SendMessageDTO>>(Result.Loading)
    val sendMessageState: StateFlow<Result<SendMessageDTO>> = _sendMessageState

    private val _deleteChatState = MutableStateFlow<Result<Unit>>(Result.Loading)
    val deleteChatState: StateFlow<Result<Unit>> = _deleteChatState

    fun createChat(request: createChatRequst) {
        viewModelScope.launch {
            createChatUseCase.execute(request).collect { _createChatState.value = it }
        }
    }

    fun getChats(userId: Long) {
        viewModelScope.launch {
            getChatsUseCase.execute(userId).collect { _chatsState.value = it }
        }
    }

    fun sendMessage(request: SendMessageDTO) {
        viewModelScope.launch {
            sendMessageUseCase.execute(request).collect { _sendMessageState.value = it }
        }
    }

    fun deleteChat(chatId: Long, userId: Long) {
        viewModelScope.launch {
            deleteChatUseCase.execute(chatId, userId).collect { _deleteChatState.value = it }
        }
    }
}
