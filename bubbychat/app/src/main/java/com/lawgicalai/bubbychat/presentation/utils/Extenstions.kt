package com.lawgicalai.bubbychat.presentation.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit) =
    this.clickable(
        indication = null, // 리플 제거
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick,
    )
