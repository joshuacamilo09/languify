package com.languify.identity.auth.data.model

sealed class AuthState {
    object Pending : AuthState()

    object Unauthenticated : AuthState()

    data class Authenticated(val session: Session) : AuthState()
}
