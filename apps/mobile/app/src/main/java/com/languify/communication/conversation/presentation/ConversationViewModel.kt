package com.languify.communication.conversation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.communication.conversation.domain.ConversationService
import com.languify.communication.conversation.domain.RecordingState
import com.languify.infra.audio.AudioRecorder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ConversationViewModel(
  private val conversationService: ConversationService,
  private val audioRecorder: AudioRecorder
) : ViewModel() {

  val conversation = conversationService.conversation
  val audioDelta = conversationService.audioDelta

  private var recordingJob: Job? = null

  fun startConversation() {
    conversationService.initializeConversation()
    conversationService.startConversation()
  }

  fun startRecording() {
    conversationService.updateRecordingState(RecordingState.RECORDING)

    recordingJob = viewModelScope.launch {
      try {
        audioRecorder.startRecording { base64Chunk -> conversationService.sendAudioChunk(base64Chunk) }
      } catch (e: Exception) {
        e.printStackTrace()
        conversationService.updateRecordingState(RecordingState.IDLE)
      }
    }
  }

  fun stopRecording() {
    audioRecorder.stopRecording()
    recordingJob?.cancel()
    recordingJob = null
    conversationService.updateRecordingState(RecordingState.PROCESSING)
    conversationService.commitAudio()
  }

  fun endConversation() {
    if (audioRecorder.isRecording()) {
      audioRecorder.stopRecording()
      recordingJob?.cancel()
    }

    conversationService.closeConversation()
  }

  override fun onCleared() {
    super.onCleared()
    if (audioRecorder.isRecording()) audioRecorder.stopRecording()
  }
}
