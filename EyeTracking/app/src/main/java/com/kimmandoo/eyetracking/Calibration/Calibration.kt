package com.kimmandoo.eyetracking.Calibration

import android.graphics.PointF
import android.util.Log
import com.google.mlkit.vision.face.Face
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression

val calibrationDataList = mutableListOf<CalibrationData>()

fun collectCalibrationData(face: Face, leftPupil: PointF, rightPupil: PointF, screenPoint: PointF) {
    val features = doubleArrayOf(
        leftPupil.x.toDouble(),
        leftPupil.y.toDouble(),
        rightPupil.x.toDouble(),
        rightPupil.y.toDouble(),
        face.headEulerAngleX.toDouble(),
        face.headEulerAngleY.toDouble()
    )
    val data = CalibrationData(features, screenPoint.x.toDouble(), screenPoint.y.toDouble())
    calibrationDataList.add(data)
}

class GazeEstimator {
    private val regressionX = OLSMultipleLinearRegression()
    private val regressionY = OLSMultipleLinearRegression()

    private var coefficientsX: DoubleArray? = null
    private var coefficientsY: DoubleArray? = null

    fun trainModel(calibrationData: List<CalibrationData>): Boolean {
        if (calibrationData.isEmpty()) {
            Log.e("GazeEstimator", "Calibration data is empty")
            return false
        }

        val inputFeatures = calibrationData.map { it.features }.toTypedArray()
        val targetX = calibrationData.map { it.screenX }.toDoubleArray()
        val targetY = calibrationData.map { it.screenY }.toDoubleArray()

        try {
            regressionX.newSampleData(targetX, inputFeatures)
            regressionY.newSampleData(targetY, inputFeatures)

            // 회귀 계수 추정
            coefficientsX = regressionX.estimateRegressionParameters()
            coefficientsY = regressionY.estimateRegressionParameters()
        } catch (e: Exception) {
            Log.e("GazeEstimator", "Training failed", e)
            return false
        }
        return true
    }

    fun predict(features: DoubleArray): PointF {
        if (coefficientsX == null || coefficientsY == null) {
            Log.e("GazeEstimator", "Model has not been trained")
            return PointF(0f, 0f)
        }

        // 예측을 위해 입력 특성에 절편 항 추가
        val featuresWithIntercept = doubleArrayOf(1.0) + features

        val predX = dotProduct(coefficientsX!!, featuresWithIntercept)
        val predY = dotProduct(coefficientsY!!, featuresWithIntercept)
        return PointF(predX.toFloat(), predY.toFloat())
    }

    private fun dotProduct(coefficients: DoubleArray, features: DoubleArray): Double {
        var result = 0.0
        for (i in coefficients.indices) {
            result += coefficients[i] * features[i]
        }
        return result
    }
}