package com.kimmandoo.presentation.login

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions

// NavHost 조차 Composable이다
@Composable
fun LoginNavHost() {
    val navController = rememberNavController() // backstack, 위치 정보 갖고있음
    // 이거로 화면이동 까지
    NavHost(
        navController = navController,
        startDestination = LoginRoute.WelcomeScreen.name
    ) {
        // 여기에 선언 되어 있어야지 NavHostrㅏ 찾아서 들어갈 수 있다
        composable(route = LoginRoute.WelcomeScreen.name) {
            WelcomeScreen(onNavigateToLoginScreen = {
                navController.navigate(route = LoginRoute.LoginScreen.name)
            })
        }
        composable(route = LoginRoute.LoginScreen.name) {
            LoginScreen(onNavigateToSignUpScreen = {
                navController.navigate(route = LoginRoute.SignUpScreen.name)
            })
        }
        composable(route = LoginRoute.SignUpScreen.name) {
            SignUpScreen(onNavigateToLoginScreen = {
//                navController.navigate(route = LoginRoute.LoginScreen.name)
                navController.navigate(route = LoginRoute.LoginScreen.name, navOptions = navOptions {
                    // 다 날리고 새로 LoginScreen에들어가는 것ㄷ
                    popUpTo(LoginRoute.WelcomeScreen.name) // WelcomeScreen까지 pop해서 쌓인 스크린을 날리겠다.
                })
            })
        }
    }
}