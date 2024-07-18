package com.kimmandoo.visionapi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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

    private val model = "gpt-4o"

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
            // URI에서 입력 스트림 열기
            val inputStream = contentResolver.openInputStream(uri)

            // 비트맵으로 디코딩
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // 비트맵을 JPEG 형식의 바이트 배열로 압축
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // Base64로 인코딩
            val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d(TAG, "uriToBase64: $base64String")
            // "data:image/jpeg;base64," 접두사 추가
            "data:image/jpeg;base64,$base64String"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}