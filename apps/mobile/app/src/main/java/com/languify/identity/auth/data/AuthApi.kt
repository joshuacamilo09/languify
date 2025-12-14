package com.languify.identity.auth.data

import com.languify.identity.auth.data.dto.GetSessionResponse
import com.languify.identity.auth.data.dto.SignInRequest
import com.languify.identity.auth.data.dto.SignResponse
import com.languify.identity.auth.data.dto.SignUpRequest
import com.languify.infra.api.ApiClient
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @GET("/auth/session")
    suspend fun getSession(): GetSessionResponse

    @POST("/auth/sign-in")
    suspend fun signIn(
        @Body request: SignInRequest,
    ): SignResponse

    @POST("/auth/sign-up")
    suspend fun signUp(
        @Body request: SignUpRequest,
    ): SignResponse

    companion object {
        fun create(): AuthApi = ApiClient.createService(AuthApi::class.java)
    }
}
