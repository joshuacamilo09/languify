package com.languify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.languify.data.model.updateProfileRequest
import com.languify.data.model.deleteUserRequest
import com.languify.data.model.UserProfileResponse
import com.languify.domain.usecase.Result
import com.languify.domain.usecase.User.DeleteProfileUseCase
import com.languify.domain.usecase.User.GetProfileUseCase
import com.languify.domain.usecase.User.UpdateProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow<Result<UserProfileResponse>>(Result.Loading)
    val profileState: StateFlow<Result<UserProfileResponse>> = _profileState

    private val _updateState = MutableStateFlow<Result<Unit>>(Result.Loading)
    val updateState: StateFlow<Result<Unit>> = _updateState

    private val _deleteState = MutableStateFlow<Result<Unit>>(Result.Loading)
    val deleteState: StateFlow<Result<Unit>> = _deleteState

    fun getProfile(userId: Long) {
        viewModelScope.launch {
            getProfileUseCase.execute(userId).collect {
                _profileState.value = it
            }
        }
    }

    fun updateProfile(id: Long, request: updateProfileRequest) {
        viewModelScope.launch {
            updateProfileUseCase.execute(id, request).collect {
                _updateState.value = it
            }
        }
    }

    fun deleteProfile(id: Long, request: deleteUserRequest) {
        viewModelScope.launch {
            deleteProfileUseCase.execute(id, request).collect {
                _deleteState.value = it
            }
        }
    }
}
