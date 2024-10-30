package com.kimmandoo.presentation.main.writing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kimmandoo.domain.model.Image
import com.kimmandoo.presentation.component.MGImagePager
import com.kimmandoo.presentation.component.MGTextField
import com.kimmandoo.presentation.theme.MVIPracticeTheme
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun WritingScreen(
    viewModel: WritingViewModel,
    onBackClick: () -> Unit,
) {
    val state = viewModel.collectAsState().value

    WritingScreen(
        selectedImages = state.selectedImages,
        text = state.text,
        onTextChanged = viewModel::onTextChanged,
        onBackClick = onBackClick,
        onPostClick = viewModel::onPostClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WritingScreen(
    selectedImages: List<Image>,
    text: String,
    onBackClick: () -> Unit,
    onPostClick: () -> Unit,
    onTextChanged: (String) -> Unit,
) {
    Surface {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = {
                    Text(text = "새 글쓰기", style = MaterialTheme.typography.headlineSmall)
                },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "뒤로가기"
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = onPostClick) {
                            Text("게시")
                        }
                    })
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // 이번에도 두 덩어리로 나눈다. 위는 선택한 이미지들을 보여줄 Pager, 아래는 텍스트를 입력할 곳

                MGImagePager(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f),
                    images = selectedImages
                )
                HorizontalDivider()
                MGTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f),
                    value = text,
                    onValueChange = onTextChanged
                )
            }
        }
    }
}

@Preview
@Composable
fun WritingScreenPreview() {
    MVIPracticeTheme {
        WritingScreen(
            text = "",
            selectedImages = emptyList(),
            onTextChanged = {},
            onBackClick = {},
            onPostClick = {}
        )
    }
}