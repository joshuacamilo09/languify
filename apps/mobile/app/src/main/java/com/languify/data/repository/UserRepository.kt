package com.languify.data.repository

import com.languify.data.model.*
import com.languify.data.network.AuthController
import retrofit2.Response

class UserRepository(
    private val api: AuthController.ApiService = AuthController.api
) {

    suspend fun getProfile(id: Long): Response<UserProfileResponse> =
        api.getProfile(id)

    suspend fun updateProfile(id: Long, request: updateProfileRequest): Response<updateProfileResponse> =
        api.updateProfile(id, request)

    suspend fun deleteProfile(id: Long, request: deleteUserRequest): Response<deleteUserResponse> =
        api.deleteProfile(id, request)
}
