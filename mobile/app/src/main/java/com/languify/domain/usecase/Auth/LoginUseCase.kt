package com.languify.domain.usecase.Auth

import com.languify.data.model.UserLoginRequest
import com.languify.data.repository.AuthRepository
import com.languify.domain.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class LoginUseCase(private val repository: AuthRepository) {
    fun execute(email: String, password: String): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val response = repository.login(UserLoginRequest(email, password))
            if (response.isSuccessful) {
                val token = response.body()?.token
                if (token != null) emit(Result.Success(token))
                else emit(Result.Error("Token não encontrado"))
            } else emit(Result.Error("Erro: ${response.code()}"))
        } catch (e: IOException) {
            emit(Result.Error("Erro de rede: ${e.message}"))
        } catch (e: HttpException) {
            emit(Result.Error("Erro HTTP: ${e.message}"))
        }
    }
}