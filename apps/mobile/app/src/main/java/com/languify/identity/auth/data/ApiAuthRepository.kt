package com.languify.identity.auth.data

import com.languify.identity.auth.domain.AuthRepository
import com.languify.identity.auth.domain.SignResult
import com.languify.infra.security.TokenStorage

class ApiAuthRepository(private val api: AuthApi, private val tokenStorage: TokenStorage) :
  AuthRepository {

  override suspend fun sign(email: String, password: String): SignResult {
    return try {
      val res = api.sign(SignRequest(email, password))
      tokenStorage.saveToken(res.token)

      SignResult.Success
    } catch (e: Exception) {
      SignResult.Error(e.message ?: "Login failed")
    }
  }
}
