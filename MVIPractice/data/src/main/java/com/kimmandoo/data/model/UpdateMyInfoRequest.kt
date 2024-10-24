package com.kimmandoo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateMyInfoRequest(
    val userName: String,
    val extraUserInfo: String,
    val profileFilePath: String
)
