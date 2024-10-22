package com.kimmandoo.eyetracking

import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark

private const val TAG = "GazeEstimation"

fun estimateGaze(faces: List<Face>, imageWidth: Int, imageHeight: Int): Offset? {
    if (faces.isEmpty()) return null

    val face = faces[0]

    val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
    val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position
    Log.d(TAG, "estimateGaze: $leftEye, $rightEye")

    val faceCenter = face.boundingBox.centerX() // 얼굴 중심 좌표
    val yaw = face.headEulerAngleY // 좌우 회전 (yaw)
    val pitch = face.headEulerAngleX // 상하 회전 (pitch)

    if (leftEye != null && rightEye != null) {
        // 눈 좌표의 중간점을 계산
        val gazeX = (leftEye.x + rightEye.x) / 2
        val gazeY = (leftEye.y + rightEye.y) / 2
        Log.d(TAG, "estimateGaze: $gazeX, $gazeY")
//        // 좌표를 화면 크기에 맞게 변환
//        val normalizedX = gazeX / imageWidth
//        val normalizedY = gazeY / imageHeight
//
//        return Offset(normalizedX, normalizedY)
        // 응시 방향에 따라 좌표 조정
        val adjustedGazeX = gazeX + yaw * YAW_PITCH // yaw 값에 비례해 좌우로 이동 (임의의 계수로 조정)
        val adjustedGazeY = gazeY - pitch * YAW_PITCH // pitch 값에 비례해 상하로 이동 (임의의 계수로 조정)

        Log.d(TAG, "Adjusted Gaze: $adjustedGazeX, $adjustedGazeY")

        // 좌표를 화면 크기에 맞게 변환
        val normalizedX = adjustedGazeX / imageWidth
        val normalizedY = adjustedGazeY / imageHeight

        return Offset(normalizedX, normalizedY)
    }

    return null
}

private const val YAW_PITCH = 30