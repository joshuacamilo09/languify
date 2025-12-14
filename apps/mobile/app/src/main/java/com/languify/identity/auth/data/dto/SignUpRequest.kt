package com.languify.identity.auth.data.dto

data class SignUpRequest(
    val email: String,
    val username: String,
    val password: String,
)
