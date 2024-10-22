package com.kimmandoo.eyetracking

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kimmandoo.eyetracking.ui.theme.EyeTrackingTheme
import org.opencv.android.OpenCVLoader

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val cameraPermission = Manifest.permission.CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenCVLoader.initDebug()
        if (ContextCompat.checkSelfPermission(
                this,
                cameraPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startApp()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(cameraPermission), 1001)
        }
    }

    private fun startApp() {
        setContent {
            Log.d(TAG, "startApp: ")
            MainScreen()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startApp()
        } else {
            // 권한이 거부된 경우 처리
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EyeTrackingTheme {
        Greeting("Android")
    }
}