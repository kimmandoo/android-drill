package com.kimmandoo.composechatbot

import android.util.Log
import com.google.gson.Gson
import com.kimmandoo.composechatbot.model.gpt.ChatGpt
import com.kimmandoo.composechatbot.model.gpt.ChatGptStreamingResponse
import com.kimmandoo.composechatbot.model.gpt.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.BufferedSource

class ChatRepository {
    private val api = NetworkModule.api

    fun getChatGptResponseStream(messages: List<Message>): Flow<String> = flow {
        val request = ChatGpt(
            model = "gpt-4o-mini",
            messages = messages
        )
        val gson = Gson()
        runCatching { api.getChatGptResponse(request) }.onSuccess {
            it.body()?.let { responseBody ->
                val source: BufferedSource = responseBody.source().buffer()
                try {
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line()
                        if (line != null && line.startsWith("data:")) {
                            val dataContent = line.removePrefix("data:").trim()
                            Log.d("Streaming", "Received data: $dataContent")
                            if (dataContent == "[DONE]") break

                            val chatGptResponse =
                                gson.fromJson(dataContent, ChatGptStreamingResponse::class.java)
                            val content =
                                chatGptResponse.choices.firstOrNull()?.delta?.content ?: ""
                            emit(content) // 실시간으로 각 청크를 emit하여 뷰모델에 전달
                            delay(50)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Streaming", "Error reading stream", e)
                } finally {
                    responseBody.close()
                }
            } ?: run {
                throw Throwable("응답에 문제가 있습니다.")
            }
        }.onFailure {
            throw it
        }
    }
}