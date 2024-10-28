package com.kimmandoo.eyetracking

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.drawText

private const val TAG = "MainScreen"

@Composable
fun MainScreen() {
    var gazePoint by remember { mutableStateOf(Offset.Unspecified) }
    val context = LocalContext.current
    var smoothedGazePoint by remember { mutableStateOf(Offset.Unspecified) }
    val smoothingFactor = 0.1f // 0과 1 사이의 값. 값이 클수록 최근 좌표에 가중치가 더 커짐.
    val boundaryOffset = -100f // 화면 경계 여백 (50px)

    // 이동 평균 필터 적용 함수
    fun smoothGazePoint(newGazePoint: Offset): Offset {
        if (smoothedGazePoint.isUnspecified) {
            smoothedGazePoint = newGazePoint
        } else {
            smoothedGazePoint = Offset(
                x = smoothedGazePoint.x + smoothingFactor * (newGazePoint.x - smoothedGazePoint.x),
                y = smoothedGazePoint.y + smoothingFactor * (newGazePoint.y - smoothedGazePoint.y)
            )
        }
        return smoothedGazePoint
    }

    val analyzer = remember {
        FaceAnalyzer { faces ->
            // 이미지 크기 가져오기 (예시로 480x640 사용)
            Log.d(TAG, "MainScreen: $faces")
            val estimatedGaze = estimateGaze(faces)
            if (estimatedGaze != null) {
                // 화면 크기에 맞게 좌표 변환
                val screenWidth = context.resources.displayMetrics.widthPixels
                val screenHeight = context.resources.displayMetrics.heightPixels
                // 좌우 반전을 위해 X 좌표를 반전
                val mirroredX = screenWidth - (estimatedGaze.x * screenWidth)
                // 새로 계산한 시선 좌표
                // 점이 화면 경계를 넘어가지 않도록 제한
                val constrainedX = mirroredX.coerceIn(boundaryOffset, screenWidth - boundaryOffset)
                val constrainedY = (estimatedGaze.y * screenHeight).coerceIn(
                    boundaryOffset,
                    screenHeight - boundaryOffset
                )

                gazePoint = Offset(
                    x = constrainedX,
                    y = constrainedY
                )

                // 시선 좌표를 부드럽게 처리
                smoothedGazePoint = smoothGazePoint(gazePoint)
                Log.d(TAG, "Smoothed GazePoint: $smoothedGazePoint")
            }
        }
    }

    // 카메라 프리뷰 없이 빨간 점을 표시할 영역 설정
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 배경을 단색으로 설정
    ) {
        CameraPreview(analyzer = analyzer) // 카메라 연결은 그대로 유지

        // 눈동자 위치를 나타내는 빨간 점 그리기
        if (smoothedGazePoint.isSpecified) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.Red,
                    radius = 15f,
                    center = smoothedGazePoint,
                )
                // x, y 좌표 텍스트 그리기
                drawIntoCanvas { canvas ->
                    val paint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        textSize = 40f
                        color = android.graphics.Color.BLACK
                    }
                    val text = "x: ${smoothedGazePoint.x.toInt()}, y: ${smoothedGazePoint.y.toInt()}"
                    canvas.nativeCanvas.drawText(
                        text,
                        smoothedGazePoint.x,
                        smoothedGazePoint.y + 50, // 점 아래에 텍스트 위치
                        paint
                    )
                }
            }
        }
    }
}