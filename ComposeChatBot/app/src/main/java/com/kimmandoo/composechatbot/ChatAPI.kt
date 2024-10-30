package com.kimmandoo.composechatbot

import com.kimmandoo.composechatbot.model.gpt.ChatGpt
import com.kimmandoo.composechatbot.model.gpt.ChatGptResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatAPI {
    @POST("v1/chat/completions")
    @Headers("Accept: text/event-stream")
    suspend fun getChatGptResponse(
        @Body request: ChatGpt
    ): Response<ResponseBody>
}