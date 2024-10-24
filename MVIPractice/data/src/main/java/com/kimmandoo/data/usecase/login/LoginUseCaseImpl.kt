package com.kimmandoo.data.usecase.login

import com.kimmandoo.data.ktor.UserService
import com.kimmandoo.data.model.LoginRequest
import com.kimmandoo.domain.usecase.login.LoginUseCase
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(
    private val userService: UserService
) : LoginUseCase {
    override suspend fun invoke(id: String, password: String): Result<String> = kotlin.runCatching {
        userService.login(LoginRequest(id, password)).data.orEmpty()
    }
}