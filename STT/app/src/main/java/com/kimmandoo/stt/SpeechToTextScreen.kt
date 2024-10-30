package com.kimmandoo.stt

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.BuildCompat
import java.util.Locale


private const val TAG = "SpeechToTextScreen"

@Composable
fun SpeechToTextScreen() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
// 화면이 회전해도 고정된 UI를 유지
    val rotationAngle = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) -90f else 0f

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    var recognizedText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }

    val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        Log.d(TAG, "SpeechToTextScreen: ${VERSION.SDK_INT}") // flip은 34
        putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
        if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            putExtra(RecognizerIntent.EXTRA_SEGMENTED_SESSION, true)
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.FORMATTING_OPTIMIZE_QUALITY
            )
            putExtra(RecognizerIntent.EXTRA_MASK_OFFENSIVE_WORDS, true)
        }
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        if (VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // 34
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_DETECTION_ALLOWED_LANGUAGES, true)
        }
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.KOREAN)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN)
        putExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES, true)

    }

    val recognitionListener = object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Log.d(TAG, "onResults: $results")
            recognizedText = matches?.firstOrNull() ?: ""
//            isListening = false
        }

        /**
         *     public static final int ERROR_AUDIO = 3;
         *     public static final int ERROR_CANNOT_CHECK_SUPPORT = 14;
         *     public static final int ERROR_CANNOT_LISTEN_TO_DOWNLOAD_EVENTS = 15;
         *     public static final int ERROR_CLIENT = 5;
         *     public static final int ERROR_INSUFFICIENT_PERMISSIONS = 9;
         *     public static final int ERROR_LANGUAGE_NOT_SUPPORTED = 12;
         *     public static final int ERROR_LANGUAGE_UNAVAILABLE = 13;
         *     public static final int ERROR_NETWORK = 2;
         *     public static final int ERROR_NETWORK_TIMEOUT = 1;
         *     public static final int ERROR_NO_MATCH = 7;
         *     public static final int ERROR_RECOGNIZER_BUSY = 8;
         *     public static final int ERROR_SERVER = 4;
         *     public static final int ERROR_SERVER_DISCONNECTED = 11;
         *     public static final int ERROR_SPEECH_TIMEOUT = 6;
         *     public static final int ERROR_TOO_MANY_REQUESTS = 10;
         */

        override fun onError(error: Int) {
            Log.d("STT", "Error code: $error")
            isListening = false
        }

        override fun onReadyForSpeech(params: Bundle?) {
            Log.d("STT", "Ready for speech")
        }

        override fun onBeginningOfSpeech() {
            Log.d("STT", "Beginning of speech")
        }

        override fun onRmsChanged(rmsdB: Float) {
            // 음성 인식 중에 마이크의 입력 음량을 나타내는 RMS(Root Mean Square) 값이 변경될 때 호출
            // db단위로 뭔가 할 수 있음
            Log.d("STT", "RMS changed: $rmsdB")
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            Log.d("STT", "Buffer received")
        }

        override fun onEndOfSpeech() {
            Log.d("STT", "End of speech")
            // EndOfSpeech가 호출되면 다시 시작
            isListening = false
            Handler(Looper.getMainLooper()).postDelayed({
                isListening = true
                speechRecognizer.startListening(speechIntent)
            }, 500) // 500ms 지연 후 다시 시작
        }

        override fun onPartialResults(partialResults: Bundle?) {
//            Log.d("STT", "Partial results $partialResults")
            val partialText = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
            Log.d("STT", "Partial results: $partialText")
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            Log.d("STT", "Event type: $eventType")
        }
    }

    DisposableEffect(Unit) {
        speechRecognizer.setRecognitionListener(recognitionListener)
        onDispose {
            speechRecognizer.destroy()
        }
    }

    SpeechToTextUI(
        recognizedText = recognizedText,
        isListening = isListening,
        onStartListening = {
            isListening = true
            speechRecognizer.startListening(speechIntent)
        },
        onStopListening = {
            isListening = false
            speechRecognizer.stopListening()
        },
        rotationAngle = rotationAngle
    )
}
