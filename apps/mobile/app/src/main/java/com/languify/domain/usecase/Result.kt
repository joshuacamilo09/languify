package com.languify.domain.usecase

sealed class Result<out T> {
    object Idle : Result<Nothing>() // ADICIONA ISTO (Estado inicial)
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}
