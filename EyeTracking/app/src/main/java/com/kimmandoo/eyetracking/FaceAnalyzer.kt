package com.kimmandoo.eyetracking

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

private const val TAG = "FaceAnalyzer"

class FaceAnalyzer(
    private val onFacesDetected: (List<Face>) -> Unit,
) : ImageAnalysis.Analyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL) // 랜드마크 모드 활성화 -> 눈 코 입 검출
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        @androidx.camera.core.ExperimentalGetImage
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            detector.process(image)
                .addOnSuccessListener { faces ->
                    Log.d(TAG, "Number of faces detected: ${faces.size}") // 얼굴 자체는 탐지 성공
                    onFacesDetected(faces)
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