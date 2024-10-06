package com.kimmandoo.componentpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.kimmandoo.componentpractice.ui.theme.ComposePracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposePracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Row(modifier = Modifier.padding(innerPadding)) {
                        // innerpadding을 주면 system bar 안으로 들어오게 한다
                        ButtonPractice()
                        CoilPractice()
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonPractice() {
    Button(
        onClick = { },
        contentPadding = PaddingValues(8.dp)
    ) {
        // Button 안에 들어갈 내용
        // 머터리얼 아이콘 팩 사용가능
        Icon(imageVector = Icons.Filled.Build, contentDescription = null)
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        // Spacer가 아이템 간 간격을 넣어주는 역할을 한다.
    }
}

@Composable
fun CoilPractice() {
    val painter = rememberAsyncImagePainter(
        // 원래는
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://developer.android.com/images/brand/Android_Robot.png")
            .crossfade(true)
            .build()
    )
    // 웹 로드니까 비동기로 긁어오는 게 안전함
    AsyncImage(
        model = "https://developer.android.com/images/brand/Android_Robot.png",
        contentDescription = null,
        placeholder = ColorPainter(androidx.compose.ui.graphics.Color.Green),
        contentScale = ContentScale.Crop,
        modifier = Modifier.clip(CircleShape).padding(8.dp)
    )
    // painter로 넣는건 예전 방식. rememberAsyncImagePainter도 안쓰고 그냥 AsyncImage로 가능
    Image(painter = painter, contentDescription = null)
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Image(painter = painter, contentDescription = null)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposePracticeTheme {
        ButtonPractice()
    }
}