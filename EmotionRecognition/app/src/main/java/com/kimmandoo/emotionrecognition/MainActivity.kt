package com.kimmandoo.emotionrecognition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kimmandoo.emotionrecognition.ui.theme.EmotionRecognitionTheme

class MainActivity : ComponentActivity() {
    private lateinit var mClassifier: TFLiteImageClassifier
    private val modelFileName = "simple_classifier.tflite"
    private val labels = arrayOf("angry", "disgust", "fear","happy","neutral","sad","surprise")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mClassifier = TFLiteImageClassifier(
            assets,
            modelFileName,
            labels
        )

        setContent {
            EmotionRecognitionTheme {
                EmotionRecognitionScreen(mClassifier)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mClassifier.close()
    }
}