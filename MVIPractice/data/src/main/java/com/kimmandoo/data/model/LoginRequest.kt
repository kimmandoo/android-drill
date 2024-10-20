package com.kimmandoo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ktor 쓸거면 request body 도 직렬화 해서 넘겨줘야 잘 돌아간다 이건 로그가 안뜸
@Serializable
data class LoginRequest(
    @SerialName("loginId")
    val id: String,
    @SerialName("password")
    val password: String
)
