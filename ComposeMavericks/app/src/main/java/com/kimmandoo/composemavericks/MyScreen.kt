package com.kimmandoo.composemavericks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyScreen(viewModel: MyViewModel = mavericksViewModel()) {

    /**
     * 	LaunchedEffect(Unit)는 컴포저블이 처음 실행될 때, 즉 화면이 그려질 때 한 번 실행되는 효과가 있음
     * 	Unit을 키로 사용하는 것은 이 효과가 컴포저블이 처음 실행될 때만 발생하도록 보장
     */
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val state by viewModel.collectAsState()

    if (state.isLoading) {
        CircularProgressIndicator()
    } else if (state.errorMessage != null) {
        Text("Error: ${state.errorMessage}")
    } else {
        LazyColumn {
            stickyHeader { 
                Text(text = "이것은 스티키 헤더다", modifier = Modifier.fillMaxWidth().background(Color.Red))
            }
            items(state.data) { item ->
                Text(item)
            }
        }
    }
}