package com.languify.communication.conversation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.communication.conversation.domain.ConversationService
import com.languify.communication.conversation.domain.RecordingState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversationViewModel(private val conversationService: ConversationService) : ViewModel() {

  val conversation = conversationService.conversation
  val audioDelta = conversationService.audioDelta

  fun startConversation() {
    conversationService.initializeConversation()
    conversationService.startConversation()
  }

  fun startRecording() {
    conversationService.updateRecordingState(RecordingState.RECORDING)
  }

  fun stopRecording() {
    conversationService.updateRecordingState(RecordingState.PROCESSING)
    conversationService.commitAudio()
  }

  fun sendAudioChunk(audioBase64: String) {
    conversationService.sendAudioChunk(audioBase64)
  }

  fun endConversation() {
    conversationService.closeConversation()
  }
}
