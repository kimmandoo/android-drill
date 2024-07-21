package com.kimmandoo.visionapi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ImageProcessingUtility(private val context: Context) {

    fun processImageForVisionAPI(imageUri: Uri, maxWidth: Int = 512, maxHeight: Int = 512, quality: Int = 85): ByteArray {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        var bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Step 1: Resize the image
        bitmap = resizeBitmap(bitmap, maxWidth, maxHeight)

        // Step 2: Compress the image
        bitmap = convertToGrayscale(bitmap)
        val compressedImage = compressBitmap(bitmap, quality)

        // Step 3: Convert to grayscale (optional)
        // Uncomment the next line if you want to convert to grayscale


        return compressedImage
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaleWidth = maxWidth.toFloat() / width
        val scaleHeight = maxHeight.toFloat() / height
        val scaleFactor = scaleWidth.coerceAtMost(scaleHeight)

        val matrix = Matrix()
        matrix.postScale(scaleFactor, scaleFactor)

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    private fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val compressFormat = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Bitmap.CompressFormat.WEBP_LOSSY
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> Bitmap.CompressFormat.WEBP
            else -> Bitmap.CompressFormat.WEBP
        }
        bitmap.compress(compressFormat, quality, outputStream)
        return outputStream.toByteArray()
    }

    private fun convertToGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val gray = (Color.red(pixel) * 0.299 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114).toInt()
                grayscaleBitmap.setPixel(x, y, Color.rgb(gray, gray, gray))
            }
        }

        return grayscaleBitmap
    }

    fun cropBitmap(bitmap: Bitmap, left: Int, top: Int, width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(bitmap, left, top, width, height)
    }

    // 이미지의 EXIF 정보를 고려한 회전 처리
    fun rotateBitmapIfRequired(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.use { stream ->
            val exif = ExifInterface(stream)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        return bitmap
    }
}