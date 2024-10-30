package com.kimmandoo.stt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun SpeechToTextUI(
    recognizedText: String,
    isListening: Boolean,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    rotationAngle: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .rotate(rotationAngle),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isListening) "Listening..." else "Press the button to start listening",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = recognizedText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { if (isListening) onStopListening() else onStartListening() }) {
            Text(text = if (isListening) "Stop Listening" else "Start Listening")
        }

//        // 화면이 회전해도 텍스트가 고정된 위치로 표시됩니다.
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = if (isPortrait) "세로 모드 고정" else "가로 모드에서도 세로로 고정됨")
//        }
    }
}
