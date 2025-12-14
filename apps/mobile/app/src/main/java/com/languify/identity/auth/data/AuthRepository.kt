package com.languify.identity.auth.data

import com.languify.identity.auth.data.dto.GetSessionResponse
import com.languify.identity.auth.data.dto.SignInRequest
import com.languify.identity.auth.data.dto.SignResponse
import com.languify.identity.auth.data.dto.SignUpRequest
import com.languify.infra.storage.TokenStorage

class AuthRepository(private val tokenStorage: TokenStorage) {
    private val api = AuthApi.create()

    suspend fun getSession(): Result<GetSessionResponse> {
        return try {
            val res = api.getSession()
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(
        email: String,
        password: String,
    ): Result<SignResponse> {
        return try {
            val res = api.signIn(SignInRequest(email, password))
            tokenStorage.putToken(res.token)

            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(
        email: String,
        username: String,
        password: String,
    ): Result<SignResponse> {
        return try {
            val res = api.signUp(SignUpRequest(email, username, password))
            tokenStorage.putToken(res.token)

            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
