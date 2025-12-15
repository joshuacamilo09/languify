package com.languify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.data.model.SendMessageDTO
import com.languify.data.model.createChatRequst
import com.languify.data.repository.ChatRepository // ✅ Importante
import com.languify.domain.usecase.*
import com.languify.domain.usecase.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    // Mantém os teus UseCases antigos
    private val createChatUseCase: CreateChatUseCase,
    private val getChatsUseCase: GetChatsUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,

    // NOVO: Precisas disto para o WebSocket
    private val chatRepository: ChatRepository
) : ViewModel() {

    // ESTADOS REST (JÁ EXISTENTES)
    private val _createChatState = MutableStateFlow<Result<com.languify.data.model.createChatResponse>>(Result.Loading)
    val createChatState: StateFlow<Result<com.languify.data.model.createChatResponse>> = _createChatState

    private val _chatsState = MutableStateFlow<Result<List<com.languify.data.model.getChatsResponse>>>(Result.Loading)
    val chatsState: StateFlow<Result<List<com.languify.data.model.getChatsResponse>>> = _chatsState

    private val _sendMessageState = MutableStateFlow<Result<SendMessageDTO>>(Result.Loading)
    val sendMessageState: StateFlow<Result<SendMessageDTO>> = _sendMessageState

    private val _deleteChatState = MutableStateFlow<Result<Unit>>(Result.Loading)
    val deleteChatState: StateFlow<Result<Unit>> = _deleteChatState

    // NOVO ESTADO: Onde guardamos a resposta em tempo real do GPT
    private val _realtimeResponse = MutableStateFlow<String>("")
    val realtimeResponse: StateFlow<String> = _realtimeResponse


    // FUNÇÕES REST (JÁ EXISTENTES)

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

    // NOVAS FUNÇÕES REALTIME (WEBSOCKET)

    fun startRealtimeSession() {
        chatRepository.connectToRealtime { message ->
            // Callback: Ocorre sempre que o Backend/GPT envia algo
            // Usamos postValue ou viewModelScope para garantir que atualiza a UI
            viewModelScope.launch {
                _realtimeResponse.value = message
            }
        }
    }

    fun sendRealtimeEvent(jsonEvent: String) {
        // Envia JSON manual (ex: evento de áudio ou texto)
        chatRepository.sendRealtimeEvent(jsonEvent)
    }

    // Fecha a conexão quando o utilizador sai do ecrã para poupar bateria/dados
    override fun onCleared() {
        super.onCleared()
        chatRepository.disconnectRealtime()
    }
}