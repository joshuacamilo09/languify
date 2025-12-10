package com.languify.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// MODELO DE DADOS
data class Translation(
    val original: String,
    val translated: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HistoryViewModel : ViewModel() {

    private val _historyList = MutableStateFlow<List<Translation>>(emptyList())
    val historyList = _historyList.asStateFlow()

    // Função para adicionar nova tradução
    fun addTranslation(item: Translation) {
        // Adiciona ao topo da lista
        _historyList.value = listOf(item) + _historyList.value
    }

    fun clearHistory() {
        _historyList.value = emptyList()
    }
}