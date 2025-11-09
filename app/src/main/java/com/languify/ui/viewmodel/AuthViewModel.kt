package com.languify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.domain.usecase.Auth.LoginUseCase
import com.languify.domain.usecase.Auth.RegisterUseCase
import com.languify.domain.usecase.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    // Estado de login
    private val _loginState = MutableStateFlow<Result<String>>(Result.Loading)
    val loginState: StateFlow<Result<String>> = _loginState

    // Estado de registo
    private val _registerState = MutableStateFlow<Result<String>>(Result.Loading)
    val registerState: StateFlow<Result<String>> = _registerState

    // 🔹 LOGIN
    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginUseCase.execute(email, password).collect {
                _loginState.value = it
            }
        }
    }

    // 🔹 REGISTER
    fun register(name: String, email: String, password: String, nativeIdiom: String) {
        viewModelScope.launch {
            registerUseCase.execute(name, email, password, nativeIdiom).collect {
                _registerState.value = it
            }
        }
    }
}
