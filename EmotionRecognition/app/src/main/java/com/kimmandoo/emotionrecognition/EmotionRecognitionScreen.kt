package com.kimmandoo.emotionrecognition

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private const val TAG = "EmotionRecognitionScree"

@Composable
fun EmotionRecognitionScreen(mClassifier: TFLiteImageClassifier) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var classificationResult by remember { mutableStateOf<Map<String, Float>?>(null) }
    val context = LocalContext.current

    // 카메라 권한 요청 런처 설정
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            captureImage(context) { uri ->
                imageUri = uri
            }
        } else {
            Toast.makeText(context, "카메라 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageUri?.let { uri ->
                    try {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            if (bitmap == null) {
                                Log.e(TAG, "Failed to decode bitmap from input stream")
                            } else {
                                Log.d(TAG, "Bitmap loaded successfully: $bitmap")
                                val rotatedBitmap =
                                    rotateImage(context.contentResolver, bitmap, uri)
                                classificationResult = mClassifier.classify(rotatedBitmap)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading bitmap from input stream", e)
                    }
                }
            }
        }

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
            uri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                val rotatedBitmap = rotateImage(context.contentResolver, bitmap, uri) // 이미지 회전 추가
                classificationResult = mClassifier.classify(rotatedBitmap)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        imageUri?.let { uri ->
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap == null) {
                        Log.e(TAG, "Failed to decode bitmap from input stream")
                    } else {
                        Log.d(TAG, "Bitmap loaded successfully: $bitmap")
                        val rotatedBitmap = rotateImage(context.contentResolver, bitmap, uri)
                        classificationResult = mClassifier.classify(rotatedBitmap)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading bitmap from input stream", e)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { pickImageLauncher.launch("image/*") }) {
            Text("이미지 선택")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                captureImage(context) { uri ->
                    imageUri = uri
                    takePictureLauncher.launch(uri)
                }
            } else {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }) {
            Text("사진 촬영")
        }

        classificationResult?.let { result ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("감정 분석 결과:", style = MaterialTheme.typography.headlineSmall)
            result.forEach { (emotion, probability) ->
                Text(
                    "$emotion: ${String.format("%.1f", probability * 100)}%",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

fun captureImage(context: Context, onImageFileCreated: (Uri) -> Unit) {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val photoFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    val imageUri = FileProvider.getUriForFile(
        context,
        "com.kimmandoo.emotionrecognition.fileprovider",
        photoFile
    )
    onImageFileCreated(imageUri)
}