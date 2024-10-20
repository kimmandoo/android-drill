package com.kimmandoo.presentation.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun MGTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    visualTranformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.White, // 아직 선택 안됐을 떄 색상
            focusedContainerColor = Color.White, // 선택 됐을 때 색상
        ),
        visualTransformation = visualTranformation,
        shape = RoundedCornerShape(8.dp)
    )
}