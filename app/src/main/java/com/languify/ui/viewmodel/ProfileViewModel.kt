package com.languify.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.core.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val context: Context) : ViewModel() {

    private val prefs = PreferencesManager(context)

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow("en")
    val language = _language.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        // Carrega preferências persistentes ao iniciar
        loadPreferences()
    }

    fun loadPreferences() {
        viewModelScope.launch {
            prefs.isDarkMode.collect { _isDarkMode.value = it }
        }
        viewModelScope.launch {
            prefs.language.collect { _language.value = it }
        }
        viewModelScope.launch {
            prefs.isLoggedIn.collect { _isLoggedIn.value = it }
        }
    }


    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_isDarkMode.value
            _isDarkMode.value = newValue
            prefs.setDarkMode(newValue)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            _language.value = lang
            prefs.setLanguage(lang)
        }
    }

    fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModelScope.launch {
                // Atualiza localmente primeiro (resposta imediata)
                _isLoggedIn.value = true

                // Depois grava no DataStore (em background)
                prefs.setLoggedIn(true)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.setLoggedIn(false)
            _isLoggedIn.value = false
        }
    }
}
