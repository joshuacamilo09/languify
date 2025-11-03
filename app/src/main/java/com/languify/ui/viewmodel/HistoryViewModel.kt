package com.languify.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.languify.viewmodel.Translation


/**
 * Guarda e gere o histórico de traduções.
 */
class HistoryViewModel : ViewModel() {

    private val _historyList = MutableStateFlow<List<Translation>>(emptyList())
    val historyList = _historyList.asStateFlow()

    fun addTranslation(item: Translation) {
        _historyList.value = _historyList.value + item
    }

    fun clearHistory() {
        _historyList.value = emptyList()
    }
}
