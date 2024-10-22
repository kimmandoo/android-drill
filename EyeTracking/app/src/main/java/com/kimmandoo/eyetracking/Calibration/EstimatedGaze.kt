package com.kimmandoo.eyetracking.Calibration

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset

data class EstimatedGaze(
    val leftPupil: PointF,
    val rightPupil: PointF,
    val gazePoint: Offset
)