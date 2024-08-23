package com.kimmandoo.composechatbot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimmandoo.composechatbot.model.MessageType
import com.kimmandoo.composechatbot.model.gpt.Message
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    var messages by mutableStateOf(listOf<MessageType>())
        private set

    var inputText by mutableStateOf("")
        private set

    val chatRepository = ChatRepository()

    fun onInputChange(newInput: String) {
        inputText = newInput
    }

    fun sendMessage() {
        if (inputText.isNotBlank()) {
            viewModelScope.launch {
                messages = messages + MessageType(0, inputText)
                val response = getGPTResponse(inputText)  // 여기서 GPT API 호출
                messages = messages + MessageType(1, response)
                inputText = ""
            }
        }
    }

    private suspend fun getGPTResponse(message: String): String {
        val response = CompletableDeferred<String>()
        viewModelScope.launch {
            val messages = listOf(
                Message(role = "system", content = "모든 답변은 200글자 이내로 해줘" + "그리고 말투는 ~습니다. 로 마무리해"),
                Message(role = "user", content = message)
            )
            runCatching {
                chatRepository.getChatGptResponse(messages).choices.first().message.content
            }.onSuccess {
                response.complete(it)
            }.onFailure {
                response.complete("오류가 발생했습니다. 잠시후 다시 시작해주세요")
            }
        }
        return response.await()
    }
}