package com.kimmandoo.composechatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimmandoo.composechatbot.ui.theme.ComposeChatBotTheme
import com.kimmandoo.composechatbot.ui.theme.MyBlue
import com.kimmandoo.composechatbot.ui.theme.Orange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeChatBotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatBot(
                        viewModel = ChatViewModel(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBot(viewModel: ChatViewModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "GPT와의 대화를 시작하세요",
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight(600)
                    )
                }
                items(viewModel.messages) { message ->
                    when (message.type) {
                        0 -> UserChat(message = message.message)
                        1 -> GptChat(message = message.message)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                TextField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.onInputChange(it) },
                    placeholder = { Text("메시지를 입력하세요") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.sendMessage() }) {
                    Text("전송")
                }
            }
        }
    }
}

@Composable
fun UserChat(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp)
    ) {
        Text(
            text = message,
            Modifier
                .align(Alignment.CenterEnd)
                .background(
                    color = MyBlue,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp, 4.dp),
            color = Color.White,
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
    )
}

@Composable
fun GptChat(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp)
    ) {
        Text(
            text = message,
            Modifier
                .align(Alignment.CenterStart)
                .background(
                    color = Orange,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp, 4.dp),
            color = Color.White,
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
    )
}