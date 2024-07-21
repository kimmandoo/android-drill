package com.kimmandoo.visionapi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.kimmandoo.visionapi.model.Content
import com.kimmandoo.visionapi.model.ImageUrl
import com.kimmandoo.visionapi.model.Message
import com.kimmandoo.visionapi.model.Payload
import com.kimmandoo.visionapi.data.NetworkModule.api
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val model = "gpt-4o-mini"

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d(TAG, "Selected URI: $uri")
                visionAPITest(ImageUrl(url = uriToBase64(uri)!!))
            } else {
                Log.d(TAG, "No media selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.main_tv_title).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun visionAPITest(imageUrl: ImageUrl) {
        lifecycleScope.launch {

            val content = mutableListOf<Content>()
            content.add(Content(image_url = null, text = "사진 속 일정을 알려줘. 제목: \n 일시: \n 장소: \n 형식으로 알려줘", type = "text"))
            content.add(
                Content(
                    image_url = imageUrl,
                    text = null,
                    type = "image_url"
                )
            )
            val message = Message(content = content, role = "user")
            val visionRequest = Payload(
                max_tokens = 300,
                messages = listOf(message),
                model = model
            )
            runCatching {
                api.getChatCompletion(visionRequest)
            }.onSuccess { response ->
                response.body()?.let { body ->
                    findViewById<TextView>(R.id.main_tv_title).text = body.choices.first().message.content
                }
            }
        }
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val utility = ImageProcessingUtility(this)
            val byteArray = utility.processImageForVisionAPI(uri)
            // Base64로 인코딩
            val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            Log.d(TAG, "uriToBase64: $base64String")
            return "data:image/webp;base64,$base64String"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun removeExifData(uri: Uri): Bitmap? {
        val inputStream = contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = false }
        return BitmapFactory.decodeStream(inputStream, null, options)
    }
}