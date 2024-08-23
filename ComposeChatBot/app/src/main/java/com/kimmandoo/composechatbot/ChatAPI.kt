package com.kimmandoo.composechatbot

import com.kimmandoo.composechatbot.model.gpt.ChatGpt
import com.kimmandoo.composechatbot.model.gpt.ChatGptResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatAPI {
    @POST("v1/chat/completions")
    suspend fun getChatGptResponse(
        @Body request: ChatGpt
    ): Response<ChatGptResponse>
}