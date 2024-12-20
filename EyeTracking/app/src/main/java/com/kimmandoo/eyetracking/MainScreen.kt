package com.kimmandoo.eyetracking

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import kotlin.math.floor

private const val TAG = "MainScreen"

@Composable
fun MainScreen() {
    val gridRows = 5 // 격자의 행 개수
    val gridCols = 5 // 격자의 열 개수
    val cellCounts = remember { mutableStateListOf(*Array(gridRows * gridCols) { 0 }) }
    var gazePoint by remember { mutableStateOf(Offset.Unspecified) }
    val context = LocalContext.current
    var smoothedGazePoint by remember { mutableStateOf(Offset.Unspecified) }
    val smoothingFactor = 0.1f // 0과 1 사이의 값. 값이 클수록 최근 좌표에 가중치가 더 커짐.
    val boundaryOffset = -80f // 화면 경계 여백 (50px)
    var zeroPoint by remember { mutableStateOf(Offset.Zero) } // 영점 조절을 위한 기준 좌표

    val configuration = LocalConfiguration.current
// 화면이 회전해도 고정된 UI를 유지
    val rotationAngle =
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) -90f else 0f

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
                // 영점 조절 기준 좌표 적용
                val adjustedX = mirroredX - zeroPoint.x
                val adjustedY = (estimatedGaze.y * screenHeight) - zeroPoint.y

                // 새로 계산한 시선 좌표
                // 점이 화면 경계를 넘어가지 않도록 제한
                // 화면 경계를 넘지 않도록 제한
                val constrainedX = adjustedX.coerceIn(boundaryOffset, screenWidth - boundaryOffset)
                val constrainedY = adjustedY.coerceIn(boundaryOffset, screenHeight - boundaryOffset)


                gazePoint = Offset(
                    x = constrainedX,
                    y = constrainedY
                )

                // 시선 좌표를 부드럽게 처리
                smoothedGazePoint = smoothGazePoint(gazePoint)
                Log.d(TAG, "Smoothed GazePoint: $smoothedGazePoint")
                // 격자 셀 인덱스 계산 및 카운트 증가
                val cellWidth = screenWidth / gridCols
                val cellHeight = screenHeight / gridRows
                val col = floor(smoothedGazePoint.x / cellWidth).toInt().coerceIn(0, gridCols - 1)
                val row = floor(smoothedGazePoint.y / cellHeight).toInt().coerceIn(0, gridRows - 1)
                val cellIndex = row * gridCols + col

                // 셀의 카운트를 증가
                cellCounts[cellIndex]++
                Log.d(TAG, "Cell $cellIndex count: ${cellCounts[cellIndex]}")
            }
        }
    }

    // 카메라 프리뷰 없이 빨간 점을 표시할 영역 설정
    Box(
        modifier = Modifier
            .fillMaxSize()
            .rotate(rotationAngle)
            .background(Color.White) // 배경을 단색으로 설정
    ) {
        CameraPreview(analyzer = analyzer) // 카메라 연결은 그대로 유지
        // 중앙에 영점 조절 버튼을 추가
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

        ) {
            Button(onClick = {
                // 화면 중앙 좌표 계산
                val screenWidth = context.resources.displayMetrics.widthPixels
                val screenHeight = context.resources.displayMetrics.heightPixels
                val centerPoint = Offset(screenWidth / 2f, screenHeight / 2f)

                // 현재 gazePoint를 영점으로 설정하고 빨간 점을 화면 중앙으로 이동
                zeroPoint = smoothedGazePoint
                gazePoint = centerPoint
                smoothedGazePoint = centerPoint
            }) {
                Text("중앙")
            }
        }


        // 격자 및 시선을 나타내는 빨간 점과 카운트 텍스트 그리기
        Canvas(modifier = Modifier.fillMaxSize()) {
            val screenWidth = size.width
            val screenHeight = size.height
            val cellWidth = screenWidth / gridCols
            val cellHeight = screenHeight / gridRows

            // 격자 그리기
            for (row in 0 until gridRows) {
                for (col in 0 until gridCols) {
                    val cellIndex = row * gridCols + col
                    val left = col * cellWidth
                    val top = row * cellHeight

                    // 셀의 테두리와 배경 그리기
                    drawRect(
                        color = Color.LightGray,
                        topLeft = Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(cellWidth, cellHeight)
                    )

                    // 각 셀의 카운트 텍스트 그리기
                    drawIntoCanvas { canvas ->
                        val paint = Paint().asFrameworkPaint().apply {
                            isAntiAlias = true
                            textSize = 30f
                            color = android.graphics.Color.BLACK
                        }
                        val text = cellCounts[cellIndex].toString()
                        canvas.nativeCanvas.drawText(
                            text,
                            left + cellWidth / 2 - 10,
                            top + cellHeight / 2 + 10,
                            paint
                        )
                    }
                }
            }

            // 시선 위치를 나타내는 빨간 점 그리기
            if (smoothedGazePoint.isSpecified) {
                drawCircle(
                    color = Color.Red,
                    radius = 15f,
                    center = smoothedGazePoint
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
                        smoothedGazePoint.y + 50,
                        paint
                    )
                }
            }
        }
    }
}