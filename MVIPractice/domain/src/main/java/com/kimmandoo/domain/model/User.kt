package com.kimmandoo.domain.model

data class User(
    val id: Long,
    val loginId: String,
    val username: String,
    val profileImageUrl: String? = null,
)
