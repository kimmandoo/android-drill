package com.kimmandoo.tensorflowlite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kimmandoo.tensorflowlite.databinding.ActivityMainBinding
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {
    private lateinit var tflite: Interpreter
    private lateinit var tokenizer: TokenizerHelper
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. 모델 및 토크나이저 로드
        val tfliteModel = FileUtil.loadMappedFile(this, "spoiler_detection_model.tflite")
        val options = Interpreter.Options()
        tflite = Interpreter(tfliteModel, options)
        tokenizer = TokenizerHelper(this, "vocab.json")

        binding.analyzeButton.setOnClickListener {
            onAnalyzeButtonClick()
        }
    }

    // 2. 텍스트 분석 함수
    private fun analyzeSpoilerContent(text: String): Float {
        // 텍스트 토큰화 및 패딩
        val tokenizedAndPadded = tokenizer.tokenizeAndPad(text)

        // 입력 텐서 준비
        val inputBuffer = ByteBuffer.allocateDirect(4 * tokenizedAndPadded.size)
            .order(ByteOrder.nativeOrder())
        tokenizedAndPadded.forEach { token ->
            inputBuffer.putFloat(token.toFloat())
        }
        inputBuffer.rewind()  // 버퍼 포인터를 처음으로 되돌림

        // 출력 텐서 준비 (shape [1, 1]에 맞게 수정)
        val outputBuffer = Array(1) { FloatArray(1) }

        // 추론 실행
        tflite.run(inputBuffer, outputBuffer)

        // 결과 해석 (0~1 사이의 스포일러 확률)
        return outputBuffer[0][0]
    }

    // 3. UI 업데이트 함수
    private fun updateUI(spoilerProbability: Float) {
        runOnUiThread {
            val resultText = when {
                spoilerProbability > 0.8 -> "높은 스포일러 가능성"
                spoilerProbability > 0.5 -> "중간 스포일러 가능성"
                else -> "낮은 스포일러 가능성"
            }
            binding.resultTextView.text = resultText
        }
    }

    // 4. 분석 버튼 클릭 핸들러
    private fun onAnalyzeButtonClick() {
        val userText = binding.inputEditText.text.toString()
        try {
            val spoilerProbability = analyzeSpoilerContent(userText)
            updateUI(spoilerProbability)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.resultTextView.text = "분석 중 오류 발생: ${e.message}"
        }
    }

    // 5. 리소스 해제
    override fun onDestroy() {
        super.onDestroy()
        tflite.close()
    }
}