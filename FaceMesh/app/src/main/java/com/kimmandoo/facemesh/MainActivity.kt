package com.kimmandoo.facemesh

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import com.google.mlkit.vision.facemesh.FaceMeshPoint


private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var previewView: PreviewView
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var meshDetector: FaceMeshDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        previewView = findViewById(R.id.view_finder)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        meshDetector = FaceMeshDetection.getClient(
            FaceMeshDetectorOptions.Builder()
                .setUseCase(FaceMeshDetectorOptions.FACE_MESH)
                .build()
        )

        if (checkPermission()) {
            openCamera()
        } else {
            requestPermissions()
        }
    }

    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun bindCameraUseCases() {
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                meshDetector.process(image)
                    .addOnSuccessListener { faceMeshes ->
                        for (faceMesh in faceMeshes) {
                            val leftEye = faceMesh.getPoints(FaceMesh.LEFT_EYE)
                            val rightEye = faceMesh.getPoints(FaceMesh.RIGHT_EYE)
                            Log.d(TAG, "face: $leftEye || $rightEye")

                            val leftEyeOpen = isEyeOpen(leftEye)
                            val rightEyeOpen = isEyeOpen(rightEye)

                            Log.d(TAG, "Left eye open: $leftEyeOpen, Right eye open: $rightEyeOpen")

                            // 양쪽 눈의 상태에 따라 전체적인 눈 감김 여부 판단
                            val bothEyesOpen = leftEyeOpen && rightEyeOpen
                            Log.d(TAG, "Both eyes open: $bothEyesOpen")

                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "error: $e")
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.d(TAG, "error: $e")
        }
    }

    private fun isEyeOpen(eyePoints: List<FaceMeshPoint>): Boolean {
        // 눈의 세로 양 끝
        val topPoint = eyePoints.minByOrNull { it.position.y }
        val bottomPoint = eyePoints.maxByOrNull { it.position.y }

        // 눈의 가로 양 끝
        val leftPoint = eyePoints.minByOrNull { it.position.x }
        val rightPoint = eyePoints.maxByOrNull { it.position.x }

        if (topPoint != null && bottomPoint != null && leftPoint != null && rightPoint != null) {
            val eyeHeight = bottomPoint.position.y - topPoint.position.y
            val eyeWidth = rightPoint.position.x - leftPoint.position.x
            val aspectRatio = eyeHeight / eyeWidth

            // 종횡비가 임계값보다 크면 눈이 떠져있음.
            val isOpen = aspectRatio > EYE_ASPECT_RATIO_THRESHOLD

            Log.d(TAG, "Eye aspect ratio: $aspectRatio, isOpen: $isOpen")
            return isOpen
        }

        return false
    }

    private fun checkPermission() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (checkPermission()) {
                openCamera()
            } else {
                Toast.makeText(
                    this,
                    "카메라 권한 필요",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val EYE_ASPECT_RATIO_THRESHOLD = 0.2f
    }
}

