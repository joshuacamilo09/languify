package com.languify.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.languify.viewmodel.Translation


data class Translation(
    val original: String,
    val translated: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HomeViewModel : ViewModel() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _translation = MutableStateFlow<Translation?>(null)
    val translation = _translation.asStateFlow()

    fun toggleRecording(historyViewModel: HistoryViewModel) {
        _isRecording.update { !it }

        if (!_isRecording.value) {
            simulateTranslation(historyViewModel)
        } else {
            _translation.value = null
        }
    }

    private fun simulateTranslation(historyViewModel: HistoryViewModel) {
        val originalText = "Olá, como estás?"
        val translatedText = "Hello, how are you?"
        val result = Translation(originalText, translatedText)
        _translation.value = result

        // Envia para o History
        historyViewModel.addTranslation(result)
    }
}
