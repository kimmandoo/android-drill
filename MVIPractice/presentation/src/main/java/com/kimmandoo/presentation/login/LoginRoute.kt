package com.kimmandoo.presentation.login

// 경로 저장해두는 곳
sealed class LoginRoute(val name: String){
    object WelcomeScreen: LoginRoute("WelcomeScreen") // 이 name이 key가 될 것
    object LoginScreen: LoginRoute("LoginScreen")
    object SignUpScreen: LoginRoute("SignUpScreen")
}