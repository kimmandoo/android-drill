package com.kimmandoo.eyetracking

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private const val TAG = "MainScreen"

@Composable
fun MainScreen() {
    var gazePoint by remember { mutableStateOf(Offset.Unspecified) }
    val context = LocalContext.current
    var smoothedGazePoint by remember { mutableStateOf(Offset.Unspecified) }
    val smoothingFactor = 0.2f // 0과 1 사이의 값. 값이 클수록 최근 좌표에 가중치가 더 커짐.

    fun smoothGazePoint(newGazePoint: Offset): Offset {
        if (smoothedGazePoint.isUnspecified) {
            // 첫 번째 좌표일 경우, 새로운 좌표로 바로 초기화
            smoothedGazePoint = newGazePoint
        } else {
            // EMA 적용
            val newX =
                smoothedGazePoint.x + smoothingFactor * (newGazePoint.x - smoothedGazePoint.x)
            val newY =
                smoothedGazePoint.y + smoothingFactor * (newGazePoint.y - smoothedGazePoint.y)
            smoothedGazePoint = Offset(newX, newY)
        }
        return smoothedGazePoint
    }

    val analyzer = remember {
        FaceAnalyzer { faces ->
            // 이미지 크기 가져오기 (예시로 480x640 사용)
            val imageWidth = 480
            val imageHeight = 640
            Log.d(TAG, "MainScreen: $faces")
            val estimatedGaze = estimateGaze(faces, imageWidth, imageHeight)
            if (estimatedGaze != null) {
                // 화면 크기에 맞게 좌표 변환
                val screenWidth = context.resources.displayMetrics.widthPixels
                val screenHeight = context.resources.displayMetrics.heightPixels
                // 좌우 반전을 위해 X 좌표를 반전
                val mirroredX = screenWidth - (estimatedGaze.x * screenWidth)
                // 새로 계산한 시선 좌표
                gazePoint = Offset(
                    x = mirroredX,
                    y = estimatedGaze.y * screenHeight
                )

                // 시선 좌표를 부드럽게 처리
                smoothedGazePoint = smoothGazePoint(gazePoint)
                Log.d(TAG, "Smoothed GazePoint: $smoothedGazePoint")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(analyzer = analyzer)
        if (gazePoint.isSpecified) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.Red,
                    radius = 20f,
                    center = gazePoint
                )
            }
        }
    }
}