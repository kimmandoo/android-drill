package com.kimmandoo.tmapwithnavermap

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.kimmandoo.tmapwithnavermap.model.TmapRouteRequest
import com.kimmandoo.tmapwithnavermap.model.TmapRouteResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestTmapAPI()

    }

    private fun ktorBasic() {
        val client = HttpClient(Android)
        lifecycleScope.launch {
            val response: String = client.get("https://ktor.io/").bodyAsText()
            Log.d(TAG, "ktorBasic: $response")
        }
    }

    private fun requestTmapAPI() {
        val client = HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }
        }
        lifecycleScope.launch {
            val response = client.post("https://apis.openapi.sk.com/transit/routes") {
                headers {
                    append("Content-Type", "application/json")
                    append("appKey", BuildConfig.TMAP)
                }
                setBody(
                    TmapRouteRequest(
                        endX = "127.030406594109",
                        endY = "37.609094989686",
                        startX = "127.02550910860451",
                        startY = "37.63788539420793"
                    )
                )
            }

            Log.d(TAG, "ktorPostAPI: ${response.body<TmapRouteResponse>()}")
        }
    }
}