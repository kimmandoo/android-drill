package com.kimmandoo.mlkittextrecognition

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityAnnotation
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractor
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var image: InputImage
    private lateinit var ocrResult: String
    private lateinit var output: TextView
    private lateinit var entityExtractor: EntityExtractor
    private val recognizer =
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d(TAG, "Selected URI: $uri")
                image = InputImage.fromFilePath(baseContext, uri)
                val result = recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        ocrResult = removeAllLineBreaks(visionText.text)
                        Log.d(TAG, "OCR result: ${ocrResult}")
                        extractEntities(ocrResult)
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "OCR error: $e")
                    }
            } else {
                Log.d(TAG, "No media selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        initializeEntityExtractor()
        output = findViewById(R.id.main_tv_title)
        entityExtractor
            .downloadModelIfNeeded()
            .addOnSuccessListener { _ ->

            }
            .addOnFailureListener { _ ->
                Log.d(TAG, "EntityExtractor: FAILED")
            }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.main_tv_title).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun removeAllLineBreaks(text: String): String {
        return text.replace(Regex("\\s*\\n\\s*"), " ").trim()
    }

    private fun initializeEntityExtractor() {
        val entityExtractorOptions = EntityExtractorOptions.Builder(EntityExtractorOptions.KOREAN)
            .build()
        entityExtractor = EntityExtraction.getClient(entityExtractorOptions)
    }

    fun extractEntities(text: String) {
        val params = EntityExtractionParams.Builder(text)
            .build()

        entityExtractor.downloadModelIfNeeded()
            .addOnSuccessListener {
                entityExtractor.annotate(params)
                    .addOnSuccessListener { result ->
                        processExtractedEntities(result)
                    }
                    .addOnFailureListener { e ->
                        println("Entity extraction failed: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                println("Model download failed: ${e.message}")
            }
    }

    private fun processExtractedEntities(result: List<EntityAnnotation>) {
        val dates = mutableListOf<String>()
        val locations = mutableListOf<String>()

        for (entityAnnotation in result) {
            for (entity in entityAnnotation.entities) {
                when (entity.type) {
                    Entity.TYPE_DATE_TIME -> {
                        val dateTime = entityAnnotation.annotatedText
                        dates.add(dateTime)
                    }

                    Entity.TYPE_ADDRESS -> {
                        val location = entityAnnotation.annotatedText
                        locations.add(location)
                    }
                }
            }
        }

        // 결과 출력
        output.apply {
            append("일정: ${if (dates.isNotEmpty()) dates.joinToString(" ~ ") else "일정: 추출된 날짜 없음"} \n")
            append("장소: ${if (locations.isNotEmpty()) locations.joinToString(", ") else "추출된 장소 없음"}")
        }
    }
}