package com.kimmandoo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FileResponse(
    val id: Long,
    val fileName: String,
    val createdAt: String,
    val filePath: String
)
