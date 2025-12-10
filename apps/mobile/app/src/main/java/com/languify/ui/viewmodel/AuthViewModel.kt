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

    // CORREÇÃO: Iniciar com Idle (Parado), não Loading
    private val _loginState = MutableStateFlow<Result<String>>(Result.Idle)
    val loginState: StateFlow<Result<String>> = _loginState

    private val _registerState = MutableStateFlow<Result<String>>(Result.Idle)
    val registerState: StateFlow<Result<String>> = _registerState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Reset state to loading before call
            _loginState.value = Result.Loading
            loginUseCase.execute(email, password).collect {
                _loginState.value = it
            }
        }
    }

    fun register(name: String, email: String, password: String, nativeIdiom: String) {
        viewModelScope.launch {
            // Reset state to loading before call
            _registerState.value = Result.Loading
            registerUseCase.execute(name, email, password, nativeIdiom).collect {
                _registerState.value = it
            }
        }
    }

    // Função utilitária para limpar o estado se sairmos do ecrã
    fun resetState() {
        _registerState.value = Result.Idle
        _loginState.value = Result.Idle
    }
}