package com.kimmandoo.emotionrecognition

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream

fun rotateImage(contentResolver: ContentResolver, imageBitmap: Bitmap, imageUri: Uri): Bitmap {
    val orientationAngle = getOrientationAngle(contentResolver, imageUri)
    val matrix = Matrix().apply { postRotate(orientationAngle.toFloat()) }
    return Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.width, imageBitmap.height, matrix, true)
}

private fun getOrientationAngle(contentResolver: ContentResolver, uri: Uri): Int {
    // 혹시 모르니까 정방향으로 돌리는 로직
    contentResolver.openInputStream(uri)?.use { inputStream ->
        val exif = ExifInterface(inputStream)
        return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }
    return 0
}
