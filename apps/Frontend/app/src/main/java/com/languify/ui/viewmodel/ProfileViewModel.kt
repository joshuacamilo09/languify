package com.languify.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.core.utils.ThemeManager
import com.languify.core.localization.LanguageManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel that manages both theme and language preferences.
 */
class ProfileViewModel(context: Context) : ViewModel() {

    // Observes dark mode preference in real time
    val darkModeEnabled: StateFlow<Boolean> = ThemeManager.getDarkMode(context)
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Observes language preference in real time
    val selectedLanguage: StateFlow<String> = LanguageManager.getLanguage(context)
        .stateIn(viewModelScope, SharingStarted.Lazily, "en")

    // Saves dark mode choice
    fun toggleDarkMode(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            ThemeManager.setDarkMode(context, enabled)
        }
    }

    // Saves selected language
    fun setLanguage(context: Context, lang: String) {
        viewModelScope.launch {
            LanguageManager.setLanguage(context, lang)
        }
    }
}
