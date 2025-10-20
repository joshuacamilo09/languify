package com.languify.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _original = MutableStateFlow("")
    val original = _original.asStateFlow()

    private val _translated = MutableStateFlow("")
    val translated = _translated.asStateFlow()

    fun simulateTranslate() {
        _original.value = "Olá, como estás?"
        _translated.value = "Hello, how are you?"
    }
}
