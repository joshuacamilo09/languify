package com.languify.domain.usecase.User

import com.languify.data.repository.UserRepository
import com.languify.data.model.UserProfileResponse
import com.languify.domain.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class GetProfileUseCase(private val repository: UserRepository) {
    fun execute(userId: Long): Flow<Result<UserProfileResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = repository.getProfile(userId)
            if (response.isSuccessful) emit(Result.Success(response.body()!!))
            else emit(Result.Error("Erro: ${response.code()}"))
        } catch (e: IOException) {
            emit(Result.Error("Erro de rede: ${e.message}"))
        } catch (e: HttpException) {
            emit(Result.Error("Erro HTTP: ${e.message}"))
        }
    }
}
