package com.kimmandoo.visionapi.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Payload(
    @SerialName("max_tokens")
    val max_tokens: Int,
    @SerialName("messages")
    val messages: List<Message>,
    @SerialName("model")
    val model: String
)

@Serializable
data class Message(
    @SerialName("content")
    val content: List<Content>,
    @SerialName("role")
    val role: String
)

@Serializable
data class ImageUrl(
    @SerialName("url")
    val url: String
)

@Serializable
data class Content(
    @SerialName("image_url")
    val image_url: ImageUrl?=null,
    @SerialName("text")
    val text: String?=null,
    @SerialName("type")
    val type: String
)