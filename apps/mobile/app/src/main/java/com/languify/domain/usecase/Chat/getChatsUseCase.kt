package com.languify.domain.usecase

import com.languify.data.model.getChatsResponse
import com.languify.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class GetChatsUseCase(private val repository: ChatRepository) {
    fun execute(userId: Long): Flow<Result<List<getChatsResponse>>> = flow {
        emit(Result.Loading)
        try {
            val response = repository.getChats(userId)
            if (response.isSuccessful) emit(Result.Success(response.body()!!))
            else emit(Result.Error("Erro: ${response.code()}"))
        } catch (e: IOException) {
            emit(Result.Error("Erro de rede: ${e.message}"))
        } catch (e: HttpException) {
            emit(Result.Error("Erro HTTP: ${e.message}"))
        }
    }
}
