package com.kimmandoo.eyetracking

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.Composable
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.ByteArrayOutputStream

private const val TAG = "FaceAnalyzer"
class FaceAnalyzer(
    private val onFacesDetected: (List<Face>, image: Bitmap) -> Unit,
) : ImageAnalysis.Analyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL) // 랜드마크 모드 활성화 -> 눈 코 입 검출
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL) // 컨투어 모드 활성화 -> 눈 윤곽따기
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        @androidx.camera.core.ExperimentalGetImage
//        val mediaImage = imageProxy.image
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val bitmap = imageProxyToBitmap(imageProxy)

        if (bitmap != null) {
            val rotatedBitmap = rotateBitmap(bitmap, rotationDegrees.toFloat())
            val image = InputImage.fromBitmap(rotatedBitmap, 0) // 이미 회전 적용했으므로 0도
            detector.process(image)
                .addOnSuccessListener { faces ->
                    Log.d(TAG, "Number of faces detected: ${faces.size}")
                    onFacesDetected(faces, rotatedBitmap) // Bitmap 전달
                }
                .addOnFailureListener { e ->
                    Log.e("FaceAnalyzer", "Face detection failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}

fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
    val yBuffer = imageProxy.planes[0].buffer
    val uBuffer = imageProxy.planes[1].buffer
    val vBuffer = imageProxy.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(rotationDegrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}