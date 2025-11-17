package com.languify.identity.auth.domain

sealed class SignResult {
  data object Success : SignResult()

  data class Error(val message: String) : SignResult()
}
