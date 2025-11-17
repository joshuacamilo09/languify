package com.languify.identity.auth.data

import com.languify.identity.user.domain.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class SignRequest(val email: String, val password: String)

data class SignResponse(val token: String)

data class SessionResponse(val user: User)

interface AuthApi {
  @POST("/auth/sign") suspend fun sign(@Body request: SignRequest): SignResponse

  @GET("/auth/session") suspend fun getSession(): SessionResponse
}
