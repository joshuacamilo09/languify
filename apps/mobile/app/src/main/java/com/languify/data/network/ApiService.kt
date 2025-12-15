package com.languify.data.network

import com.languify.data.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object AuthController {

    private const val BASE_URL = "http://172.20.10.3:8080/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    interface ApiService {

        // AUTH
        @POST("auth/login")
        suspend fun login(@Body request: UserLoginRequest): Response<UserLoginResponse>

        @POST("auth/register")
        suspend fun register(@Body request: UserRegisterRequest): Response<UserRegisterResponse>

        @POST("auth/google")
        suspend fun loginGoogle(@Body request: UserLoginGoole): Response<UserLoginResponse>

        // USER
        @GET("user/profile/{id}")
        suspend fun getProfile(@Path("id") id: Long): Response<UserProfileResponse>

        @PUT("user/updateProfile/{id}")
        suspend fun updateProfile(
            @Path("id") id: Long,
            @Body request: updateProfileRequest
        ): Response<updateProfileResponse>

        @DELETE("user/deleteUser/{id}")
        suspend fun deleteProfile(
            @Path("id") id: Long,
            @Body request: deleteUserRequest
        ): Response<deleteUserResponse>

        // CHAT
        @POST("chat/createChat")
        suspend fun createChat(@Body request: createChatRequst): Response<createChatResponse>

        @GET("chat/getChats/user/{userId}")
        suspend fun getChatsUser(@Path("userId") userId: Long): Response<List<getChatsResponse>>

        @GET("chat/getMessage/chat/{chatId}/message")
        suspend fun searchMessageInChat(
            @Path("chatId") chatId: Long,
            @Query("message") message: String
        ): Response<getMessageResponse>

        @POST("chat/sendMessage")
        suspend fun sendMessage(@Body request: SendMessageDTO): Response<SendMessageDTO>

        @DELETE("chat/deleteChat/{chatId}")
        suspend fun deleteChat(
            @Path("chatId") chatId: Long,
            @Query("userId") userId: Long
        ): Response<DeleteChatResponse>
    }

}
