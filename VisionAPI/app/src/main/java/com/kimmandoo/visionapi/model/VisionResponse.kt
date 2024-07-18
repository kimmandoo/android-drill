package com.kimmandoo.visionapi.model

import kotlinx.serialization.Serializable

@Serializable
data class VisionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val usage: Usage,
    val choices: List<Choice>
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class Choice(
    val message: MessageResponse,
    val finish_details: FinishDetails,
    val index: Int
)

@Serializable
data class MessageResponse(
    val role: String,
    val content: String
)

@Serializable
data class FinishDetails(
    val type: String,
    val stop: String
)