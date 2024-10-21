package com.kimmandoo.data.model

import com.kimmandoo.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val loginId: String,
    val username: String,
    val extraUserInfo: String,
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
