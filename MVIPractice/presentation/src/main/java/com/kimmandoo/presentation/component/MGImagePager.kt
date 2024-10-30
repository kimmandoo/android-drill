package com.kimmandoo.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.kimmandoo.domain.model.Image
import com.kimmandoo.presentation.theme.MVIPracticeTheme

@Composable
fun MGImagePager(modifier: Modifier, images: List<Image>) {
    val pagerState = rememberPagerState(
        pageCount = { images.size }
    )
    Box(modifier = modifier) {
        HorizontalPager(
            modifier = modifier,
            state = pagerState,
            key = {
                images[it].uri // uri가 같으면 리컴포지션 x
            }) { idx ->
            val image = images[idx]
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(
                    model = image.uri,
                    contentScale = ContentScale.Crop
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop // 꽉채워서 자르기
            )
        }
        Box(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd)
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                text = "${pagerState.currentPage + 1} / ${images.size}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White // TextColor를 의미함
            ) // currentPage는 zero base다
        }
    }
}

@Preview
@Composable
fun MGImagePagerPreview() {
    MVIPracticeTheme {
//        MGImagePager(modifier =Modifier, pagerState = , images = listOf())
    }
}