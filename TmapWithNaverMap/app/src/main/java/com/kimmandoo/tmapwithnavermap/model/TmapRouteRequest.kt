package com.kimmandoo.tmapwithnavermap.model

import kotlinx.serialization.Serializable

@Serializable
data class TmapRouteRequest(
    val count: Int = 1,
    val endX: String,
    val endY: String,
    val format: String = "json",
    val lang: Int = 0,
    val startX: String,
    val startY: String
)