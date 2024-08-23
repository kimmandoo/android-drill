package com.kimmandoo.composechatbot

import com.kimmandoo.composechatbot.model.gpt.ChatGpt
import com.kimmandoo.composechatbot.model.gpt.ChatGptResponse
import com.kimmandoo.composechatbot.model.gpt.Message
import kotlinx.coroutines.CompletableDeferred

class ChatRepository {
    private val api = NetworkModule.api

    suspend fun getChatGptResponse(messages: List<Message>): ChatGptResponse {
        val request = ChatGpt(
            model = "gpt-4o-mini",
            messages = messages
        )
        val gptAnswer = CompletableDeferred<ChatGptResponse>()
        runCatching { api.getChatGptResponse(request) }.onSuccess {
            it.body()?.let {
                gptAnswer.complete(it)
            }
            it.errorBody()?.let {
                gptAnswer.completeExceptionally(Throwable("잘못된 값으로 요청했습니다."))
            }
        }
        return gptAnswer.await()
    }
}