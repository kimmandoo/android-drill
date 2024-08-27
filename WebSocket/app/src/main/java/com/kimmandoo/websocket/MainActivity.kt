package com.kimmandoo.websocket

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var webSocketClient: WebSocketClient
    private lateinit var webSocketImageClient: WebSocketImageClient
    private lateinit var rosClient: ROSWebSocketClient
    private lateinit var rosSocketClient: ROSSocketClient

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var tvMessages: TextView

    private val messageFlow = MutableStateFlow("")

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
        tvMessages = findViewById(R.id.receivedMessagesTextView)
//        webSocket()
//        rosSocket()
        rosClient = ROSWebSocketClient("ws://$CONNTECT_IP:9090")


        initROSClient()

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {
                rosClient.publish("/test_topic", """{"data": "$message"}""")
                messageEditText.text.clear()
            }
        }


        // flow 적용버전
        lifecycleScope.launch {
            messageFlow.collectLatest { message ->
                tvMessages.append(message)
            }
        }
    }

    private fun initROSClient() {
        rosClient.apply {
            lifecycleScope.launch(Dispatchers.IO) {
                connect()
            }

            onConnectionOpened = {
                lifecycleScope.launch {
                    messageFlow.emit("ROS 연결성공")
                    subscribe("/chatter")
                }
            }

            onMessageReceived = { topic, message ->
                lifecycleScope.launch {
                    messageFlow.emit("토픽: $topic, 메시지: $message\n")
                }
            }

            onConnectionClosed = { code, reason ->
                lifecycleScope.launch {
                    messageFlow.emit("연결 종료: $code, $reason\n")
                }
            }

            onConnectionFailed = { error ->
                lifecycleScope.launch {
                    messageFlow.emit("연결 실패: ${error.message}\n")
                }
            }
        }
    }

//    private fun rosSocket() {
//        val ip = "192.168.56.102"
//        rosSocketClient = ROSSocketClient(ip, 9090)  // ROS Master URI의 IP와 포트
//
//        lifecycleScope.launch {
//            rosSocketClient.connect()
//
//            rosSocketClient.sendMessage("Hello ROS!")
//            val response = rosSocketClient.receiveMessage()
//            println("Received from ROS: $response")
//        }
//    }

    private fun webSocket() {
        webSocketClient = WebSocketClient("ws://echo.websocket.org")

        webSocketClient.onMessageReceived = { message ->
            tvMessages.append("받은 메시지: $message\n")
        }

        webSocketClient.onConnectionOpened = {
            tvMessages.append("WebSocket 연결됨\n")
        }

        webSocketClient.onConnectionClosed = { code, reason ->
            tvMessages.append("WebSocket 연결 종료: $code, $reason\n")
        }

        webSocketClient.onConnectionFailed = { error ->
            tvMessages.append("WebSocket 연결 실패: ${error.message}\n")
        }

        webSocketClient.connect()
    }

    private fun webImageSocket() {
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
        rosClient.disconnect()
    }

    companion object {
        const val CONNTECT_IP = "192.168.137.1"
    }
}