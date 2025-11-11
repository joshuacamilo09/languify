package com.languify.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.languify.core.PreferencesManager
import com.languify.data.model.UserLoginRequest
import com.languify.data.model.UserProfileResponse
import com.languify.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val context: Context,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val prefs = PreferencesManager(context)

    private val _user = MutableStateFlow<UserProfileResponse?>(null)
    val user = _user.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow("en")
    val language = _language.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadPreferences()
        checkIfLoggedIn()
    }

    private fun loadPreferences() {
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

    private fun checkIfLoggedIn() {
        viewModelScope.launch {
            prefs.isLoggedIn.collect { logged ->
                if (logged) {
                    prefs.userId.collect { id ->
                        if (id > 0) fetchUserProfile(id)
                    }
                }
            }
        }
    }

    fun saveLoginData(token: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            prefs.setToken(token)
            prefs.setLoggedIn(true)
            _isLoggedIn.value = true
            onComplete()
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(UserLoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.token
                    val userId = body?.id ?: -1L

                    if (!token.isNullOrEmpty() && userId != -1L) {
                        prefs.setToken(token)
                        prefs.setUserId(userId)
                        prefs.setLoggedIn(true)
                        _isLoggedIn.value = true
                        fetchUserProfile(userId)
                        onSuccess()
                    } else {
                        onError("Invalid token or user ID")
                    }
                } else onError("Login failed: ${response.message()}")
            } catch (e: Exception) {
                onError(e.message ?: "Unexpected error")
            }
        }
    }

    fun fetchUserProfile(id: Long) {
        viewModelScope.launch {
            try {
                val response = authRepository.getProfile(id)
                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    _errorMessage.value = "Failed to fetch profile: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.clearAll()
            _isLoggedIn.value = false
            _user.value = null
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_isDarkMode.value
            prefs.setDarkMode(newValue)
            _isDarkMode.value = newValue
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            prefs.setLanguage(lang)
            _language.value = lang
        }
    }

    fun getUserId(onResult: (Long) -> Unit) {
        viewModelScope.launch {
            prefs.userId.collect { id -> onResult(id) }
        }
    }
}

class ProfileViewModelFactory(
    private val context: Context,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(context, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
