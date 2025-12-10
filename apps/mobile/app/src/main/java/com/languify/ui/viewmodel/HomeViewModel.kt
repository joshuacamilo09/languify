package com.languify.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class RecorderState { READY, RECORDING, PLAYING }

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Estado visual do botão
    private val _state = MutableStateFlow(RecorderState.READY)
    val state = _state.asStateFlow()

    // O texto que vai aparecendo no ecrã
    private val _transcribedText = MutableStateFlow("Tap to speak...")
    val transcribedText = _transcribedText.asStateFlow()

    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)
    private var textToSpeech: TextToSpeech? = null

    // Callback: A UI vai "escutar" esta variável para saber quando guardar no histórico
    var onTextRecognized: ((String) -> Unit)? = null

    fun initialize() {
        // 1. Configurar TextToSpeech (Falar)
        textToSpeech = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
            }
        }

        // 2. Configurar SpeechRecognizer (Ouvir)
        setUpRecognitionListener()
    }

    private fun setUpRecognitionListener() {
        if (!SpeechRecognizer.isRecognitionAvailable(getApplication())) {
            _transcribedText.value = "Recognition not available"
            return
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {
                _transcribedText.value = "Listening..."
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                // O estado muda no onResults
            }

            override fun onError(error: Int) {
                _state.value = RecorderState.READY
                _transcribedText.value = "Error ($error). Try again."
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    _transcribedText.value = text

                    // 1. AVISAR A UI PARA GUARDAR NO HISTÓRICO
                    onTextRecognized?.invoke(text)

                    // 2. FALAR DE VOLTA
                    speakText(text)
                } else {
                    _state.value = RecorderState.READY
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _transcribedText.value = matches[0]
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startRecording() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        try {
            speechRecognizer.startListening(intent)
            _state.value = RecorderState.RECORDING
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = RecorderState.READY
        }
    }

    fun stopRecording() {
        speechRecognizer.stopListening()
    }

    private fun speakText(text: String) {
        _state.value = RecorderState.PLAYING
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_id")

        // Volta ao estado normal visualmente (podes ajustar isto se quiseres esperar que a fala acabe)
        _state.value = RecorderState.READY
    }

    override fun onCleared() {
        super.onCleared()
        try {
            speechRecognizer.destroy()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}