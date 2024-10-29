package com.kimmandoo.presentation.main.writing

import android.provider.MediaStore.Audio.Media
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.kimmandoo.domain.model.Image
import com.kimmandoo.presentation.theme.MVIPracticeTheme
import org.orbitmvi.orbit.compose.collectAsState


@Composable
fun ImageSelectScreen(
    viewModel: WritingViewModel,
    onBackClick: () -> Unit, // navigation까지 올려줘야됨
    onNextClick: () -> Unit
) {
    val state = viewModel.collectAsState().value

    ImageSelectScreen(
        selectedImages = state.selectedImages,
        images = state.images,
        onBackClick = onBackClick,
        onNextClick = onNextClick,
        onImageClick = viewModel::onImageClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageSelectScreen(
    selectedImages: List<Image>,
    images: List<Image>,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    onImageClick: (Image) -> Unit,
) {
    Surface {
        Scaffold(topBar = {
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
                    TextButton(onClick = onNextClick) {
                        Text("다음")
                    }
                })
        }, content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = rememberAsyncImagePainter(
                            model = selectedImages.lastOrNull()?.uri
                        ),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                    if (images.isEmpty()) {
                        Text(text = "선택된 이미지가 없습니다.")
                    }
                }
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White),
                    columns = GridCells.Adaptive(110.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp), // 2dp씩 벌ㄹ기,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(
                        count = images.size,
                        key = { idx ->
                            images[idx].uri
                            // 동일한 uri에 대해서는 리컴포지션 하지않도록
                        },
                    ) { idx ->
                        val image = images[idx]

                        Box(modifier = Modifier.clickable {
                            onImageClick(image)
                        }) {
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                painter = rememberAsyncImagePainter(
                                    model = image.uri,
                                    contentScale = ContentScale.Crop
                                ),
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                            if (selectedImages.contains(image)) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }
            }
        })
    }
}


@Preview
@Composable
fun ImageSelectScreenPreview() {
    MVIPracticeTheme {
        ImageSelectScreen(
            selectedImages = listOf(),
            images = listOf(),
            onBackClick = {},
            onNextClick = {},
            onImageClick = {})
    }
}