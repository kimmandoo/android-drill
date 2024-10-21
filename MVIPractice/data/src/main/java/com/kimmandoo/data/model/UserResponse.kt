package com.kimmandoo.data.model

import com.kimmandoo.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("loginId")
    val loginId: String,
    @SerialName("userName")
    val username: String,
    @SerialName("extraUserInfo")
    val extraUserInfo: String,
    @SerialName("profileFilePath")
    val profileFilePath: String,
)

fun UserResponse.toUser(): User {
    return User(
        id = id,
        loginId = loginId,
        username = username,
        profileImageUrl = profileFilePath,
    )
}
