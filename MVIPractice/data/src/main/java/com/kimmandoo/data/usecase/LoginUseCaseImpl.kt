package com.kimmandoo.data.usecase

import com.kimmandoo.data.ktor.UserService
import com.kimmandoo.data.model.LoginRequest
import com.kimmandoo.domain.usecase.login.LoginUseCase
import javax.inject.Inject

// jakarta.inject.Inject 쓰면 안된다.
class LoginUseCaseImpl @Inject constructor(
    private val userService: UserService
) : LoginUseCase {
    override suspend fun invoke(id: String, password: String): Result<String> = kotlin.runCatching {
        userService.login(LoginRequest(id, password)).data
    }
}