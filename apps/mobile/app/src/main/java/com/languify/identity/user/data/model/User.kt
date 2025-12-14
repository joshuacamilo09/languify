package com.languify.identity.user.data.model

import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val username: String,
)
