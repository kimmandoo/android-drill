package com.kimmandoo.emotionrecognition

import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteImageClassifier(assetManager: AssetManager, modelFileName: String, labels: Array<String>) {

    private val interpreter: Interpreter
    private val labels: List<String> = labels.toList()

    init {
//        val delegate = GpuDelegate()
//        val options = Interpreter.Options().addDelegate(delegate)
//        interpreter = Interpreter(loadModel(assetManager, modelFileName), options)
        val options = Interpreter.Options() // CPU 전용 옵션
        interpreter = Interpreter(loadModel(assetManager, modelFileName), options)
        val inputShapeValue = interpreter.getInputTensor(0).shape()
        println("Model Input Shape: ${inputShapeValue.contentToString()}")  // 예: [1, 224, 224, 3] 또는 [1, feature_length]

    }

    private fun loadModel(assetManager: AssetManager, modelFileName: String): MappedByteBuffer {
        assetManager.openFd(modelFileName).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).channel.use { fileChannel ->
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    fun classify(bitmap: Bitmap): Map<String, Float> {
        val inputArray = preprocessImage(bitmap) // 모델에 맞는 전처리된 입력 배열
        val outputMap = mutableMapOf<String, Float>()

        // 출력 배열을 모델의 출력 형식에 맞춰 설정
        val outputArray = Array(1) { FloatArray(7) } // 7개의 출력을 모델에서 예상하므로 크기를 [1, 7]로 설정합니다
        interpreter.run(inputArray, outputArray)

        // 각 label에 맞춰 output 값을 저장
        labels.forEachIndexed { index, label ->
            if (index < outputArray[0].size) { // 인덱스가 출력 배열의 크기를 넘지 않도록 제한
                outputMap[label] = outputArray[0][index]
            }
        }
        return outputMap
    }

    private val inputShape = interpreter.getInputTensor(0).shape() // 예: [1, 224, 224, 3]
    private val height = inputShape[1]
    private val width = inputShape[2]
    private val channels = inputShape[3]

    private fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        // 입력 이미지 크기를 모델의 입력 크기인 48x48로 맞춤
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 48, 48, false)
        val input = Array(1) { Array(48) { Array(48) { FloatArray(1) } } }

        for (i in 0 until 48) {
            for (j in 0 until 48) {
                val pixel = scaledBitmap.getPixel(i, j)
                // RGB 값을 그레이스케일로 변환
                val grayscale = (0.299 * ((pixel shr 16) and 0xFF) +
                        0.587 * ((pixel shr 8) and 0xFF) +
                        0.114 * (pixel and 0xFF)).toFloat() / 255.0f
                input[0][i][j][0] = grayscale
            }
        }
        return input
    }

    fun close() {
        interpreter.close()
    }
}