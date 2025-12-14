package com.languify.conversation.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.languify.conversation.domain.model.ConversationMessage
import com.languify.conversation.domain.model.ConversationState
import com.languify.infra.audio.AudioPlaybackManager
import com.languify.infra.audio.AudioRecordManager
import com.languify.infra.realtime.RealtimeClient
import com.languify.infra.realtime.data.listener.RealtimeEventListener
import com.languify.infra.realtime.data.model.RealtimeEvent
import com.languify.infra.realtime.data.model.RealtimeMessage
import com.languify.infra.realtime.data.model.RealtimeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationViewModel : ViewModel() {
    private val _state = MutableStateFlow<ConversationState>(ConversationState.Idle)
    val state: StateFlow<ConversationState> = _state.asStateFlow()

    private val _messages = MutableStateFlow<List<ConversationMessage>>(emptyList())
    val messages: StateFlow<List<ConversationMessage>> = _messages.asStateFlow()

    private val _detectedLanguage = MutableStateFlow<String?>(null)
    val detectedLanguage: StateFlow<String?> = _detectedLanguage.asStateFlow()

    private var currentTranscription = ""
    private var currentTranslation = ""
    private var hasPendingAudio = false
    private var awaitingResponse = false
    private var autoStartRecording = false
    private var readyForAudio = false

    private val eventListener =
        object : RealtimeEventListener {
            override fun onStateChanged(state: RealtimeState) {
                when (state) {
                    is RealtimeState.Connecting -> {}
                    is RealtimeState.Connected -> {}
                    is RealtimeState.Disconnected -> {
                        readyForAudio = false
                        _state.value = ConversationState.Idle
                    }

                    is RealtimeState.Failed -> {
                        readyForAudio = false
                        _state.value =
                            ConversationState.Error(state.error.message ?: "Connection failed")
                    }
                }
            }

            override fun onEvent(event: RealtimeEvent) {
                handleRealtimeEvent(event)
            }

            override fun onRawMessage(message: String) {}
        }

    init {
        RealtimeClient.addListener(eventListener)
    }

    private var shouldAutoStartRecording = false

    fun startConversation(autoStartRecording: Boolean = false) {
        shouldAutoStartRecording = autoStartRecording
        this.autoStartRecording = autoStartRecording
        readyForAudio = false

        viewModelScope.launch {
            _state.value = ConversationState.Initializing
            RealtimeClient.connect()
        }
    }

    fun startRecording() {
        if (_state.value == ConversationState.Playing) {
            AudioPlaybackManager.stopPlayback()

            val interruptMessage = RealtimeMessage("interrupt")
            RealtimeClient.sendMessage(interruptMessage)
        }

        autoStartRecording = true
        hasPendingAudio = false

        if (awaitingResponse) {
            handleInterruption()
        }

        if (!readyForAudio) {
            _state.value = ConversationState.Initializing
            shouldAutoStartRecording = true
            return
        }
        _state.value = ConversationState.Recording

        AudioRecordManager.startRecording(
            onChunk = { base64Audio ->
                if (!readyForAudio) return@startRecording
                if (awaitingResponse) handleInterruption()

                hasPendingAudio = true

                val audioData = JsonObject().apply { addProperty("audio", base64Audio) }
                RealtimeClient.sendMessage(RealtimeMessage("audio.chunk", audioData))
            },
        )
    }

    fun stopRecording() {
        AudioRecordManager.stopRecording()

        if (!awaitingResponse && hasPendingAudio) {
            sendCommit()
        } else {
            hasPendingAudio = false
            _state.value = ConversationState.Idle
        }
    }

    fun closeConversation() {
        AudioRecordManager.stopRecording()
        AudioPlaybackManager.stopPlayback()
        RealtimeClient.sendMessage(RealtimeMessage("close"))

        _state.value = ConversationState.Idle
        _messages.value = emptyList()
        _detectedLanguage.value = null
        currentTranscription = ""
        currentTranslation = ""
        hasPendingAudio = false
        awaitingResponse = false
        autoStartRecording = false
        readyForAudio = false
    }

    private fun handleRealtimeEvent(event: RealtimeEvent) {
        when (event) {
            is RealtimeEvent.ConversationInitializing -> {
                _state.value =
                    if (AudioRecordManager.isCurrentlyRecording()) {
                        ConversationState.Recording
                    } else {
                        ConversationState.AwaitingSourceLanguage
                    }
            }

            is RealtimeEvent.ConnectionInitialized -> {
                readyForAudio = true
                maybeStartRecording()
            }

            is RealtimeEvent.ConnectionInitializationFailed ->
                _state.value =
                    ConversationState.Error("Failed to initialize conversation")

            is RealtimeEvent.LanguageDetected -> {
                _detectedLanguage.value = event.language
                _state.value = ConversationState.LanguageDetected(event.language)

                // Keep recording active but reflect the real state on the controls
                if (AudioRecordManager.isCurrentlyRecording()) {
                    _state.value = ConversationState.Recording
                }
            }

            is RealtimeEvent.TranscriptionDelta -> {
                currentTranscription = event.full
            }

            is RealtimeEvent.TranslationDelta -> {
                currentTranslation = event.full
            }

            is RealtimeEvent.AudioChunk -> {
                if (_state.value != ConversationState.Playing) {
                    _state.value = ConversationState.Playing
                    AudioPlaybackManager.startPlayback {
                        _state.value = ConversationState.Idle
                        if (autoStartRecording) {
                            startRecording()
                        }
                    }
                }

                AudioPlaybackManager.addAudioChunk(event.audio)
            }

            is RealtimeEvent.ResponseComplete -> {
                awaitingResponse = false

                if (currentTranscription.isNotEmpty()) {
                    val message =
                        ConversationMessage(
                            transcription = currentTranscription,
                            translation = currentTranslation.ifEmpty { currentTranscription },
                            fromMe = false,
                        )

                    _messages.value = _messages.value + message
                }

                _state.value =
                    if (AudioPlaybackManager.isCurrentlyPlaying()) {
                        AudioPlaybackManager.finishAfterQueue()
                        AudioPlaybackManager.onQueueDrained {
                            _state.value = ConversationState.Idle
                            if (autoStartRecording) startRecording()
                        }
                        ConversationState.Playing
                    } else {
                        if (autoStartRecording) {
                            startRecording()
                        }
                        ConversationState.Idle
                    }

                currentTranscription = ""
                currentTranslation = ""
                hasPendingAudio = false
            }

            is RealtimeEvent.ErrorOccurred ->
                _state.value =
                    ConversationState.Error(event.message)

            else -> {}
        }
    }

    override fun onCleared() {
        super.onCleared()

        RealtimeClient.removeListener(eventListener)
        AudioRecordManager.stopRecording()
        AudioPlaybackManager.stopPlayback()
    }

    private fun sendCommit() {
        hasPendingAudio = false
        awaitingResponse = true
        _state.value = ConversationState.Processing

        RealtimeClient.sendMessage(RealtimeMessage("audio.commit"))
    }

    private fun handleInterruption() {
        AudioPlaybackManager.stopPlayback()
        RealtimeClient.sendMessage(RealtimeMessage("interrupt"))
        awaitingResponse = false
        hasPendingAudio = false
        currentTranscription = ""
        currentTranslation = ""
        _state.value =
            if (_detectedLanguage.value == null) {
                ConversationState.AwaitingSourceLanguage
            } else {
                ConversationState.Recording
            }
    }

    private fun maybeStartRecording() {
        if (!shouldAutoStartRecording) return

        shouldAutoStartRecording = false
        startRecording()
    }
}
