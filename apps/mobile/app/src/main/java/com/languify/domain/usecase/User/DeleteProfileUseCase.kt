package com.languify.domain.usecase.User

import com.languify.data.model.deleteUserRequest
import com.languify.data.repository.UserRepository
import com.languify.domain.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class DeleteProfileUseCase(private val repository: UserRepository) {
    fun execute(id: Long, request: deleteUserRequest): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val response = repository.deleteProfile(id, request)
            if (response.isSuccessful) emit(Result.Success(Unit))
            else emit(Result.Error("Erro: ${response.code()}"))
        } catch (e: IOException) {
            emit(Result.Error("Erro de rede: ${e.message}"))
        } catch (e: HttpException) {
            emit(Result.Error("Erro HTTP: ${e.message}"))
        }
    }
}