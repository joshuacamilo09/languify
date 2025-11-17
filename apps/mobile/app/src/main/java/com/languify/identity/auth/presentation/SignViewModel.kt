package com.languify.identity.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.languify.identity.auth.domain.AuthRepository
import com.languify.identity.auth.domain.SignResult
import com.languify.identity.auth.domain.SignStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SignState(
  val email: String = "",
  val password: String = "",
  val status: SignStatus = SignStatus.IDLE,
  val error: String? = ""
)

class SignViewModel(private val authRepository: AuthRepository) : ViewModel() {
  private val _state = MutableStateFlow(SignState())
  val state: StateFlow<SignState> = _state.asStateFlow()

  fun onEmailChange(email: String) {
    _state.value = _state.value.copy(email = email, error = null)
  }

  fun onPasswordChange(password: String) {
    _state.value = _state.value.copy(password = password, error = null)
  }

  fun onSignClick() {
    viewModelScope.launch {
      _state.value = _state.value.copy(status = SignStatus.LOADING)

      when (val result = authRepository.sign(_state.value.email, _state.value.password)) {
        is SignResult.Success -> _state.value = _state.value.copy(status = SignStatus.SUCCESS, error = null)

        is SignResult.Error -> _state.value = _state.value.copy(status = SignStatus.ERROR, error = result.message)
      }
    }
  }
}

class SignViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SignViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return SignViewModel(authRepository) as T
    }

    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
