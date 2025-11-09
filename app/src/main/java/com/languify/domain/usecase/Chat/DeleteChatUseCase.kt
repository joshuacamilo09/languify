package com.languify.domain.usecase

import com.languify.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class DeleteChatUseCase(private val repository: ChatRepository) {
    fun execute(chatId: Long, userId: Long): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val response = repository.deleteChat(chatId, userId)
            if (response.isSuccessful) emit(Result.Success(Unit))
            else emit(Result.Error("Erro: ${response.code()}"))
        } catch (e: IOException) {
            emit(Result.Error("Erro de rede: ${e.message}"))
        } catch (e: HttpException) {
            emit(Result.Error("Erro HTTP: ${e.message}"))
        }
    }
}
