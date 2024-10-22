package com.kimmandoo.eyetracking.Calibration

import androidx.compose.ui.geometry.Offset

data class CalibrationPointState(
    val point: Offset,
    var isCollected: Boolean = false
)