package com.kimmandoo.composechatbot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimmandoo.composechatbot.model.MessageType
import com.kimmandoo.composechatbot.model.gpt.Message
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    var messages by mutableStateOf(listOf<MessageType>())
        private set

    var inputText by mutableStateOf("")
        private set

    private var currentGptMessage = "" // GPT가 작성 중인 메시지

    val chatRepository = ChatRepository()

    fun onInputChange(newInput: String) {
        inputText = newInput
    }

    fun sendMessage() {
        if (inputText.isNotBlank()) {
            viewModelScope.launch {
                // 사용자의 입력 메시지 추가
                messages = messages + MessageType(0, inputText)
                inputText = ""

                // 새로운 메시지 전송을 위해 GPT 메시지 초기화
                currentGptMessage = ""

                // 시스템 및 사용자 메시지 설정
                val messageList = listOf(
                    Message(role = "system", content = "그리고 말투는 ~습니다. 로 마무리해"),
                    Message(role = "user", content = inputText)
                )

                // 스트리밍 응답 수신
                chatRepository.getChatGptResponseStream(messageList).onEach { responseChunk ->
                    currentGptMessage += responseChunk // 새로운 청크 추가

                    // 기존 GPT 메시지를 덮어쓰지 않고 누적되도록 함
                    updateGptMessage()
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun updateGptMessage() {
        // GPT 메시지를 누적하여 마지막 메시지로 업데이트
        if (messages.isNotEmpty() && messages.last().type == 1) {
            messages = messages.dropLast(1) + MessageType(1, currentGptMessage)
        } else {
            messages = messages + MessageType(1, currentGptMessage)
        }
    }
}