package com.kimmandoo.tmapwithnavermap

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.kimmandoo.tmapwithnavermap.model.TmapRouteRequest
import com.kimmandoo.tmapwithnavermap.model.TmapRouteResponse
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlin.math.log

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun ktorBasic() {
        val client = HttpClient(Android)
        lifecycleScope.launch {
            val response: String = client.get("https://ktor.io/").bodyAsText()
            Log.d(TAG, "ktorBasic: $response")
        }
    }

    private suspend fun requestTmapAPI(): TmapRouteResponse {
        val client = HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }
        }
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

        return response.body()
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        lifecycleScope.launch {
            // Tmap에서 받아온 값으로 navermap에 오버레이를 그린다.
            val response = requestTmapAPI()
            val routeOverlay = PathOverlay()
            val routeData = mutableListOf<LatLng>()
//            Log.d(TAG, "RouteData: $response")
            val getBestRoute = response.metaData.plan.itineraries.first()
            Log.d(TAG, "getBestRoute: $getBestRoute")
            for (route in getBestRoute.legs) {
                when(route.mode){
                    Mode.WALK.mode -> {
                        for (lineString in route.steps.orEmpty()) {
                            routeData.addAll(parseLatLng(lineString.linestring))
                        }
                    }
                    else -> {
                        route.passShape?.let { lineString ->
                            routeData.addAll(parseLatLng(lineString.linestring))
                        }
                    }
                }
            }
            Log.d(TAG, "routeData: $routeData")
            routeOverlay.apply {
                coords = routeData
                color = Color.BLUE
                outlineWidth = 4
                map = naverMap
            }
        }
    }

    private fun parseLatLng(lineString: String): List<LatLng> {
        return lineString.split(" ").map { coords ->
            val (longitude, latitude) = coords.split(",")
            LatLng(latitude.toDouble(), longitude.toDouble())
        }
    }

    enum class Mode(val mode: String) {
        WALK("WALK"),
        BUS("BUS"),
        SUBWAY("SUBWAY"),
        EXPRESS_BUS("EXPRESS BUS"),
        TRAIN("TRAIN"),
        AIRPLANE("AIRPLANE"),
        FERRY("FERRY"),
    }
}