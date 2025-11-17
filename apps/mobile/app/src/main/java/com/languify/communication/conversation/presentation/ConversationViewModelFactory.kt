package com.languify.communication.conversation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.languify.communication.conversation.domain.ConversationService
import com.languify.infra.audio.AudioRecorder

class ConversationViewModelFactory(private val conversationService: ConversationService) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ConversationViewModel::class.java)) {
      val audioRecorder = AudioRecorder()
      @Suppress("UNCHECKED_CAST")
      return ConversationViewModel(conversationService, audioRecorder) as T
    }

    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
