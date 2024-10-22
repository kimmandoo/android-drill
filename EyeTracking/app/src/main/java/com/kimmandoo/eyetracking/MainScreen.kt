package com.kimmandoo.eyetracking

import android.graphics.PointF
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.face.Face
import com.kimmandoo.eyetracking.Calibration.CalibrationPointState
import com.kimmandoo.eyetracking.Calibration.EstimatedGaze
import com.kimmandoo.eyetracking.Calibration.GazeEstimator
import com.kimmandoo.eyetracking.Calibration.calibrationDataList
import com.kimmandoo.eyetracking.Calibration.collectCalibrationData
import kotlinx.coroutines.delay

private const val TAG = "MainScreen"

@Composable
fun MainScreen() {
    var gazePoint by remember { mutableStateOf(Offset.Unspecified) }
    val context = LocalContext.current
    val isCalibrating = remember { mutableStateOf(true) }
    var currentCalibrationIndex by remember { mutableStateOf(0) }
    val gazeEstimator = remember { GazeEstimator() }
    var shouldCollectData by remember { mutableStateOf(false) }
    var estimatedGazeState by remember { mutableStateOf<EstimatedGaze?>(null) }
    var faceState by remember { mutableStateOf<Face?>(null) }

    val calibrationPoints = remember {
        val points = mutableListOf<CalibrationPointState>()
        val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = context.resources.displayMetrics.heightPixels.toFloat()

        val columns = 5
        val rows = 5
        val horizontalStep = screenWidth / (columns + 1)
        val verticalStep = screenHeight / (rows + 1)

        for (i in 1..columns) {
            for (j in 1..rows) {
                val x = i * horizontalStep
                val y = j * verticalStep
                val point = Offset(x, y)
                points.add(CalibrationPointState(point))
            }
        }
        points
    }

    val analyzer = remember {
        FaceAnalyzer { faces, image ->
            val estimatedGaze = estimateGaze(faces, image)
            if (estimatedGaze != null) {
                val face = faces[0]
                if (isCalibrating.value) {
                    if (currentCalibrationIndex < calibrationPoints.size) {
                        val currentPointState = calibrationPoints[currentCalibrationIndex]
                        if (!currentPointState.isCollected) {
                            estimatedGazeState = estimatedGaze
                            faceState = face
                            shouldCollectData = true
                        }
                    }

                    if (currentCalibrationIndex >= calibrationPoints.size) {
                        Log.d(TAG, "MainScreen: ${calibrationDataList}")
                        val success = gazeEstimator.trainModel(calibrationDataList)
                        if (success) {
                            Log.e(TAG, "Model training success")
                            isCalibrating.value = false
                        } else {
                            Log.e(TAG, "Model training failed")
                        }
                    }
                } else {
                    val features = doubleArrayOf(
                        estimatedGaze.leftPupil.x.toDouble(),
                        estimatedGaze.leftPupil.y.toDouble(),
                        estimatedGaze.rightPupil.x.toDouble(),
                        estimatedGaze.rightPupil.y.toDouble(),
                        face.headEulerAngleX.toDouble(),
                        face.headEulerAngleY.toDouble()
                    )
                    val predictedPoint = gazeEstimator.predict(features)
                    gazePoint = Offset(predictedPoint.x, predictedPoint.y)
                }
            }
        }
    }

    if (shouldCollectData) {
        LaunchedEffect(currentCalibrationIndex) {
            delay(1000L)

            val currentPointState = calibrationPoints[currentCalibrationIndex]
            collectCalibrationData(
                faceState!!,
                estimatedGazeState!!.leftPupil,
                estimatedGazeState!!.rightPupil,
                PointF(currentPointState.point.x, currentPointState.point.y)
            )
            currentPointState.isCollected = true
            currentCalibrationIndex++
            shouldCollectData = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(analyzer = analyzer)
        if (isCalibrating.value) {
            val currentPoint = calibrationPoints.getOrNull(currentCalibrationIndex)?.point
            if (currentPoint != null) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.Green,
                        radius = 20f,
                        center = currentPoint
                    )
                }
                Text(
                    text = "녹색 점을 바라봐 주세요.",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        } else if (gazePoint.isSpecified) {
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
