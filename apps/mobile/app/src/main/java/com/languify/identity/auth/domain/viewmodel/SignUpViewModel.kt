package com.languify.identity.auth.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.identity.auth.data.AuthRepository
import com.languify.infra.api.data.model.PromiseState
import com.languify.infra.storage.StorageProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val _state = MutableStateFlow<PromiseState>(PromiseState.Idle)
    val state = _state.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
        _state.value = PromiseState.Pending
    }

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    fun onUsernameChange(username: String) {
        _username.value = username
        _state.value = PromiseState.Idle
    }

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onPasswordChange(password: String) {
        _password.value = password
        _state.value = PromiseState.Idle
    }

    private val repo = AuthRepository(StorageProvider.getTokenStorage())

    fun signUp(onSignUp: () -> Unit) {
        if (_state.value == PromiseState.Pending) return

        if (_email.value.isBlank()) {
            _state.value = PromiseState.Rejected("Email should be in a valid format.")
            return
        }

        if (_password.value.isBlank()) {
            _state.value = PromiseState.Rejected("Password shouldn't be empty.")
            return
        }

        viewModelScope.launch {
            _state.value = PromiseState.Pending

            repo.signUp(_email.value, _username.value, _password.value)
                .onSuccess {
                    _state.value = PromiseState.Resolved
                    onSignUp()
                }
                .onFailure {
                    _state.value = PromiseState.Rejected("Something went wrong while signing up.")
                }
        }
    }
}
