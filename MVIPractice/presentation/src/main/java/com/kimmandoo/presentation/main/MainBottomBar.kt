package com.kimmandoo.presentation.main

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kimmandoo.presentation.main.writing.WritingActivity
import com.kimmandoo.presentation.theme.MVIPracticeTheme

@Composable
fun MainBottomBar(
    navController: NavController,
) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState() // 백스택 긁어오기
    val currentRoute: MainRoute = navBackStackEntry?.destination?.route?.let { currentRoute ->
        MainRoute.entries.find { route -> route.route == currentRoute }
    } ?: MainRoute.BOARD // 현재 라우트 가져오기
    // ?:로 처리해서 non-null로 처리
    MainBottomBar(
        currentRoute = currentRoute,
        onItemClick = { newRoute ->
            if (newRoute == MainRoute.WRITING) {
                // 글쓰기의 경우 startActivity
                context.startActivity(Intent(context, WritingActivity::class.java))
            } else {
                navController.navigate(route = newRoute.route) {
                    navController.graph.startDestinationRoute?.let {
                        // 시작 위치 가져오기
                        popUpTo(it) { // 시작 위치로 이동
                            // 상태를 저장하겠냐
                            saveState = true
                        }
                        launchSingleTop = true // 하나의 그래프에서 하나만 띄울건지
                        restoreState = true
                    }
                }
            }
        }
    )
}

@Composable
fun MainBottomBar(
    currentRoute: MainRoute,
    onItemClick: (MainRoute) -> Unit,
) {
    // 네비게이션 컨트롤러가 들어가있어야됨
    Column() {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MainRoute.entries.forEach { route ->
                IconButton(onClick = {
                    onItemClick(route) // 위로 올려줌
                }) {
                    Icon(
                        imageVector = route.icon,
                        contentDescription = route.contentDescription,
                        tint = if (currentRoute == route) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Gray // 선택된 상태를 구분하기 위해서 tint를 걸어준다
                        }
                    ) // material icons에서 가져감
                }
            }
        }
    }

}

@Preview
@Composable
private fun MainBottomBarPreview() {
    //MainBottomBar()
    var currentRoute by remember {
        mutableStateOf(MainRoute.BOARD)
    }
    MVIPracticeTheme {
        MainBottomBar(currentRoute = currentRoute, onItemClick = { route ->
            currentRoute = route
        })
    }
}