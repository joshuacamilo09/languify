package com.languify.identity.auth.data.dto

data class SignInRequest(
    val email: String,
    val password: String,
)
