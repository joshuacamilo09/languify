package com.languify.data.repository

import com.languify.data.model.*
import com.languify.data.network.AuthController
import retrofit2.Response

class AuthRepository(
    private val api: AuthController.ApiService = AuthController.api
) {
    // Login
    suspend fun login(request: UserLoginRequest): Response<UserLoginResponse> =
        api.login(request)

    // Registo
    suspend fun register(request: UserRegisterRequest): Response<UserRegisterResponse> =
        api.register(request)

    // Buscar perfil por ID
    suspend fun getProfile(id: Long): Response<UserProfileResponse> =
        api.getProfile(id)
}
