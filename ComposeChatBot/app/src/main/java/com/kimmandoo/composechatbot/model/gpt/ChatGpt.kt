package com.kimmandoo.composechatbot.model.gpt

import com.google.gson.annotations.SerializedName

data class ChatGpt(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<Message>
)

data class ChatGptResponse(
    @SerializedName("choices") val choices: List<Choice>
)

data class Message(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class Choice(
    @SerializedName("message") val message: Message
)