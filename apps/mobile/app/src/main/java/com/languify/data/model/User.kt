package com.languify.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

// LOGIN & REGISTO
data class UserLoginRequest(
    val email: String,
    val password: String
)

data class UserLoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("id") val id: Long? = null,
    @SerializedName("firstName") val name: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("native_idiom") val native_idiom: String? = null
)

data class UserRegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val native_idiom: String
)

data class UserRegisterResponse(
    @SerializedName("token") val token: String,
    @SerializedName("id") val id: Long? = null,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("email") val email: String?
)

data class UserLoginGoole(
    val IDtoken: String
)

// PERFIL DO UTILIZADOR
data class UserProfileResponse(
    val id: Long,
    val nome: String,
    val email: String,
    val RegisterDate: LocalDateTime,
    val native_idiom: String
)

// UPDATE / DELETE
data class updateProfileRequest(
    val nome: String,
    val email: String,
    val native_idiom: String
)

data class updateProfileResponse(
    val message: String
)

data class deleteUserRequest(val id: Long)
data class deleteUserResponse(val message: String)

// CHAT
data class createChatRequst(val receiverId: Long)
data class createChatResponse(
    val id: Long,
    val user1: UserDTO,
    val user2: UserDTO,
    val originIdiom: String,
    val destinationIdiom: String,
    val city: String,
    val country: String,
    val createdAt: LocalDateTime
)

data class getChatsResponse(
    val id: Long,
    val origin_Idiom: String,
    val destination_Idiom: String,
    val active: Boolean,
    val user1: UserDTO,
    val user2: UserDTO,
    val location: LocationDTO
)

data class UserDTO(
    val id: Long,
    val nome: String,
    val email: String,
    val native_idiom: String
)

data class LocationDTO(val country: String, val city: String)
data class getMessageResponse(
    val chatId: Long,
    val originalMessage: String,
    val translatedMessage: String,
    val sendAt: LocalDateTime
)

data class SendMessageDTO(
    val chatId: Long,
    val originalText: String,
    val translatedText: String,
    val sendAt: LocalDateTime,
    val direction: String
)

data class DeleteChatResponse(val message: String)
