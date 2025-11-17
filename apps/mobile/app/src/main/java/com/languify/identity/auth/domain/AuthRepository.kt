package com.languify.identity.auth.domain

interface AuthRepository {
  suspend fun sign(email: String, password: String): SignResult
}
