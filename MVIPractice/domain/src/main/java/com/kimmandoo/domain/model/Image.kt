package com.kimmandoo.domain.model

import kotlinx.serialization.Serializable

@Serializable // Data 모듈에서 사용하기 위해 직렬화를 해줬음
data class Image(
    val uri: String,
    val name: String,
    val size: Long,
    val mimeType: String
)
