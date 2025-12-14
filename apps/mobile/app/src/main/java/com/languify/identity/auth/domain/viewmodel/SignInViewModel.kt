package com.languify.identity.auth.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.identity.auth.data.AuthRepository
import com.languify.infra.api.data.model.PromiseState
import com.languify.infra.storage.StorageProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow<PromiseState>(PromiseState.Idle)
    val state: StateFlow<PromiseState> = _state.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email

    fun onEmailChange(email: String) {
        _email.value = email
        _state.value = PromiseState.Idle
    }

    private val _password = MutableStateFlow("")
    val password = _password

    fun onPasswordChange(password: String) {
        _password.value = password
        _state.value = PromiseState.Idle
    }

    private val repo = AuthRepository(StorageProvider.getTokenStorage())

    fun signIn(onSignIn: () -> Unit) {
        if (state == PromiseState.Pending) return

        if (_email.value.isBlank()) {
            _state.value = PromiseState.Rejected("Email should have a valid format.")
            return
        }

        if (_password.value.isBlank()) {
            _state.value = PromiseState.Rejected("Password shouldn't be empty.")
            return
        }

        viewModelScope.launch {
            _state.value = PromiseState.Pending

            repo.signIn(_email.value, _password.value)
                .onSuccess {
                    _state.value = PromiseState.Resolved
                    onSignIn()
                }
                .onFailure {
                    _state.value = PromiseState.Rejected("Something went wrong while signing in.")
                }
        }
    }
}
