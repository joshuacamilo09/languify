package com.languify.infra.api.data.model

sealed class PromiseState {
    object Idle : PromiseState()

    object Pending : PromiseState()

    object Resolved : PromiseState()

    data class Rejected(val message: String) : PromiseState()
}
