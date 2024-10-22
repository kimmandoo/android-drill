package com.kimmandoo.eyetracking

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.kimmandoo.eyetracking.Calibration.EstimatedGaze
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.imgproc.Imgproc

private const val TAG = "GazeEstimation"

fun estimateGaze(faces: List<Face>, image: Bitmap): EstimatedGaze? {
    if (faces.isEmpty()) return null

    val face = faces[0]

    val leftEyePupil = detectPupil(face, image, FaceContour.LEFT_EYE)
    val rightEyePupil = detectPupil(face, image, FaceContour.RIGHT_EYE)

    if (leftEyePupil != null && rightEyePupil != null) {
        // 동공 위치의 중간점을 계산
        val gazeX = (leftEyePupil.x + rightEyePupil.x) / 2
        val gazeY = (leftEyePupil.y + rightEyePupil.y) / 2

        val gazePoint = Offset(gazeX, gazeY)
        return EstimatedGaze(
            leftPupil = leftEyePupil,
            rightPupil = rightEyePupil,
            gazePoint = gazePoint
        )

    }

    return null
}

fun detectPupil(face: Face, image: Bitmap, eyeContourType: Int): PointF? {
    val contour = face.getContour(eyeContourType)?.points ?: return null
//    Log.d(TAG, "detectPupil: $contour")
    // 눈 영역을 감싸는 사각형 계산
    val rect = getBoundingRect(contour)

    // 이미지에서 눈 영역만 크롭
    val eyeBitmap = cropToBitmap(image, rect) ?: return null
    Log.d(TAG, "eyeBitmap: $eyeBitmap")

    // Bitmap을 Mat로 변환
    val eyeMat = Mat()
    Utils.bitmapToMat(eyeBitmap, eyeMat)

    // 이미지 전처리 및 이진화
    Imgproc.cvtColor(eyeMat, eyeMat, Imgproc.COLOR_BGR2GRAY)
    Imgproc.GaussianBlur(eyeMat, eyeMat, org.opencv.core.Size(5.0, 5.0), 0.0)
    Imgproc.threshold(eyeMat, eyeMat, 0.0, 255.0, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU)

    // 윤곽선 검출
    val contours = mutableListOf<MatOfPoint>()
    val hierarchy = Mat()
    Imgproc.findContours(
        eyeMat,
        contours,
        hierarchy,
        Imgproc.RETR_TREE,
        Imgproc.CHAIN_APPROX_SIMPLE
    )

    // 가장 큰 윤곽선을 동공으로 가정
    var maxArea = 0.0
    var pupilContour: MatOfPoint? = null
    for (contourItem in contours) {
        val area = Imgproc.contourArea(contourItem)
        if (area > maxArea) {
            maxArea = area
            pupilContour = contourItem
        }
    }

    // 동공의 무게 중심 계산
    val moments = Imgproc.moments(pupilContour)
    val cx = (moments.m10 / moments.m00).toFloat()
    val cy = (moments.m01 / moments.m00).toFloat()

    // 원본 이미지 좌표로 변환
    val pupilPoint = PointF(rect.left + cx, rect.top + cy)

    return pupilPoint
}

fun getBoundingRect(points: List<PointF>): Rect {
    val xs = points.map { it.x }
    val ys = points.map { it.y }

    val left = xs.minOrNull()?.toInt() ?: 0
    val right = xs.maxOrNull()?.toInt() ?: 0
    val top = ys.minOrNull()?.toInt() ?: 0
    val bottom = ys.maxOrNull()?.toInt() ?: 0
    Log.d(TAG, "getBoundingRect: $left, $top, $right, $bottom")
    return Rect(left, top, right, bottom)
}

fun cropToBitmap(bitmap: Bitmap, rect: Rect): Bitmap? {
    // 크롭 영역이 비트맵의 범위를 벗어나지 않도록 조정
    val left = rect.left.coerceAtLeast(0)
    val top = rect.top.coerceAtLeast(0)
    val right = rect.right.coerceAtMost(bitmap.width)
    val bottom = rect.bottom.coerceAtMost(bitmap.height)

    val width = right - left
    val height = bottom - top

    if (width <= 0 || height <= 0) return null

    return Bitmap.createBitmap(bitmap, left, top, width, height)
}