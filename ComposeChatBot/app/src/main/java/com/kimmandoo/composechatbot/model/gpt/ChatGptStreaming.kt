package com.kimmandoo.composechatbot.model.gpt

import com.google.gson.annotations.SerializedName

data class ChatGptStreamingResponse(
    @SerializedName("id") val id: String,
    @SerializedName("object") val obj: String,
    @SerializedName("created") val created: Long,
    @SerializedName("model") val model: String,
    @SerializedName("choices") val choices: List<Choices>
)

data class Choices(
    @SerializedName("index") val index: Int,
    @SerializedName("delta") val delta: Delta,
    @SerializedName("finish_reason") val finishReason: String?
)

data class Delta(
    @SerializedName("content") val content: String?
)