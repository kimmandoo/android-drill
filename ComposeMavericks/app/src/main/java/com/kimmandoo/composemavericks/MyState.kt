package com.kimmandoo.composemavericks

import com.airbnb.mvrx.MavericksState

data class MyState(
    val isLoading: Boolean = false,
    val data: List<String> = emptyList(),
    val errorMessage: String? = null
) : MavericksState