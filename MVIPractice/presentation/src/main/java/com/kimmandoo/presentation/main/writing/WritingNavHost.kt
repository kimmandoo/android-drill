package com.kimmandoo.presentation.main.writing

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun WritingNavHost(
    onFinish: () -> Unit, // 액티비티를 종료할 것이기 때문에 액티비티까지 이벤트를 끌어올려준다
) {
    val controller = rememberNavController()
    val sharedViewModel: WritingViewModel = viewModel()
    NavHost(
        navController = controller,
        startDestination = WritingRoute.IMAGE_SELECT_SCREEN.route
    ) {
        composable(route = WritingRoute.IMAGE_SELECT_SCREEN.route) {
            ImageSelectScreen(
                viewModel = sharedViewModel,
                onBackClick = onFinish,
                onNextClick = {
                    controller.navigate(WritingRoute.WRITING_SCREEN.route)
                }
            )
        }

        composable(route = WritingRoute.WRITING_SCREEN.route) {
            WritingScreen(viewModel = sharedViewModel, onBackClick = {
                controller.navigateUp()
            }
            )
        }
    }
}