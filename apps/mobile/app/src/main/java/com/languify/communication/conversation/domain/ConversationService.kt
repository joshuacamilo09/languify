package com.languify.communication.conversation.domain

import android.util.Base64
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

  fun clearAudioDelta() {
    _audioDelta.value = null
  }

  private val _isStarting = MutableStateFlow(false)
  val isStarting: StateFlow<Boolean> = _isStarting.asStateFlow()

  private val audioChunks = mutableListOf<String>()

  private var pendingFromLanguage: String = "pt"
  private var pendingToLanguage: String = "en"

  fun startConversation(fromLanguage: String = "pt", toLanguage: String = "en") {
    _isStarting.value = true
    pendingFromLanguage = fromLanguage
    pendingToLanguage = toLanguage

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

  fun swapLanguages() {
    webSocketService.send("conversation:swap")
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
          _conversation.value =
            ActiveConversation(
              fromLanguage = pendingFromLanguage,
              toLanguage = pendingToLanguage,
              recordingState = RecordingState.RECORDING,
              translationState = TranslationState.IDLE,
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

          _conversation.value = current.copy(translationState = translationState)
        }
        "conversation:data:delta" -> {
          val data = json["data"] as? Map<*, *>
          val audio = data?.get("audio") as? String
          if (audio != null) audioChunks.add(audio)
        }

        "conversation:data:done" -> {
          if (audioChunks.isNotEmpty()) {
            // Decode all base64 chunks to bytes, concatenate, then re-encode
            val allBytes =
              audioChunks
                .flatMap { chunk -> Base64.decode(chunk, Base64.NO_WRAP).toList() }
                .toByteArray()

            val completeAudio = Base64.encodeToString(allBytes, Base64.NO_WRAP)
            _audioDelta.value = completeAudio
            audioChunks.clear()
          }
        }

        "conversation:swap:success" -> {
          val current = _conversation.value
          if (current == null) return

          _conversation.value =
            current.copy(
              fromLanguage = current.toLanguage,
              toLanguage = current.fromLanguage,
              recordingState = RecordingState.RECORDING,
              translationState = TranslationState.IDLE,
            )
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
