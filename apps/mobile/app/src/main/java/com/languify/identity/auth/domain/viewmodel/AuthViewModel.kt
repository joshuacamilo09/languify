package com.languify.identity.auth.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.identity.auth.data.AuthRepository
import com.languify.identity.auth.data.model.AuthState
import com.languify.infra.storage.StorageProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Pending)
    val state = _state.asStateFlow()

    private val tokenStorage = StorageProvider.getTokenStorage()
    private val repo = AuthRepository(tokenStorage)

    fun validateSession() {
        if (tokenStorage.getToken() == null) {
            _state.value = AuthState.Unauthenticated
            return
        }

        viewModelScope.launch {
            repo.getSession()
                .onSuccess { res -> _state.value = AuthState.Authenticated(res.session) }
                .onFailure { _state.value = AuthState.Unauthenticated }
        }
    }

    fun signOut() {
        tokenStorage.removeToken()
        validateSession()
    }

    init {
        validateSession()
    }
}
