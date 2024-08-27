package com.kimmandoo.websocket

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webSocketClient: WebSocketClient
    private lateinit var webSocketImageClient: WebSocketImageClient
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var receivedMessagesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        receivedMessagesTextView = findViewById(R.id.receivedMessagesTextView)
        webSocket()


        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {
                webSocketClient.sendMessage(message)
                messageEditText.text.clear()
            }
        }
    }

    private fun webSocket(){
        webSocketClient = WebSocketClient("ws://echo.websocket.org")

        webSocketClient.onMessageReceived = { message ->
            receivedMessagesTextView.append("받은 메시지: $message\n")
        }

        webSocketClient.onConnectionOpened = {
            receivedMessagesTextView.append("WebSocket 연결됨\n")
        }

        webSocketClient.onConnectionClosed = { code, reason ->
            receivedMessagesTextView.append("WebSocket 연결 종료: $code, $reason\n")
        }

        webSocketClient.onConnectionFailed = { error ->
            receivedMessagesTextView.append("WebSocket 연결 실패: ${error.message}\n")
        }

        webSocketClient.connect()
    }

    private fun webImageSocket(){
        webSocketImageClient = WebSocketImageClient("ws://your-websocket-server-url")

        webSocketImageClient.onImageReceived = { bitmap ->
            // 이미지 수신한거 처리할 곳
        }

        webSocketImageClient.onConnectionOpened = {
            // 연결 성공 시 처리
        }

        webSocketImageClient.onConnectionClosed = { code, reason ->
            // 연결 종료 시 처리
        }

        webSocketImageClient.onConnectionFailed = { error ->
            // 연결 실패 시 처리
        }

        webSocketImageClient.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.disconnect()
    }
}