package com.languify.communication.conversation.domain

import com.google.gson.Gson
import com.languify.communication.conversation.data.ProcessConversationDataDTO
import com.languify.communication.conversation.data.StartConversationDTO
import com.languify.infra.websocket.WebSocketEvent
import com.languify.infra.websocket.domain.WebSocketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConversationService(private val webSocketService: WebSocketService) {
  private val gson = Gson()
  private val _conversation = MutableStateFlow<ActiveConversation?>(null)
  val conversation: StateFlow<ActiveConversation?> = _conversation.asStateFlow()

  private val _audioDelta = MutableStateFlow<String?>(null)
  val audioDelta: StateFlow<String?> = _audioDelta.asStateFlow()

  private val _isStarting = MutableStateFlow(false)
  val isStarting: StateFlow<Boolean> = _isStarting.asStateFlow()

  fun startConversation(fromLanguage: String = "pt", toLanguage: String = "en") {
    _isStarting.value = true
    val dto = StartConversationDTO(fromLanguage, toLanguage)
    webSocketService.send("conversation:start", dto)
  }

  fun sendAudioChunk(audioBase64: String) {
    val dto = ProcessConversationDataDTO(audioBase64)
    webSocketService.send("conversation:data", dto)
  }

  fun commitAudio() {
    webSocketService.send("conversation:translate")
  }

  fun closeConversation() {
    webSocketService.send("conversation:close")
    clearConversation()
  }

  fun handleWebSocketEvent(event: WebSocketEvent) {
    when (event) {
      is WebSocketEvent.OnMessage -> handleMessage(event.text)
      else -> {}
    }
  }

  private fun handleMessage(message: String) {
    try {
      val json = gson.fromJson(message, Map::class.java)
      val eventName = json["event"] as? String ?: return

      when (eventName) {
        "conversation:start:success" -> {
          _isStarting.value = false
          _conversation.value = ActiveConversation(
            fromLanguage = "pt",
            toLanguage = "en",
            recordingState = RecordingState.RECORDING,
            translationState = TranslationState.READY
          )
        }
        "conversation:translate:state" -> {
          val data = json["data"] as? Map<*, *>
          val state = data?.get("state") as? String

          val translationState =
            when (state) {
              "loading" -> TranslationState.LOADING
              "reproducing" -> TranslationState.REPRODUCING
              "done" -> TranslationState.DONE
              "ready" -> TranslationState.READY
              else -> TranslationState.IDLE
            }

          val current = _conversation.value
          if (current == null) return

          var updated = current.copy(translationState = translationState)
          if (translationState == TranslationState.READY) updated = updated.copy(fromLanguage = current.toLanguage, toLanguage = current.fromLanguage)
          _conversation.value = updated
        }
        "conversation:data:delta" -> {
          val data = json["data"] as? Map<*, *>
          val audio = data?.get("audio") as? String
          if (audio != null) _audioDelta.value = audio
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun updateRecordingState(state: RecordingState) {
    val current = _conversation.value
    if (current != null) _conversation.value = current.copy(recordingState = state)
  }

  fun initializeConversation(fromLanguage: String = "pt", toLanguage: String = "en") {
    _conversation.value = ActiveConversation(fromLanguage, toLanguage)
  }

  fun clearConversation() {
    _conversation.value = null
  }
}
