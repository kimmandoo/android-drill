package com.kimmandoo.data.usecase.login

import com.kimmandoo.data.ktor.UserService
import com.kimmandoo.data.model.SignUpRequest
import com.kimmandoo.domain.usecase.login.SignUpUseCase
import javax.inject.Inject

class SignUpUseCaseImpl @Inject constructor(
    private val userService: UserService,
) : SignUpUseCase {
    override suspend fun invoke(id: String, username: String, password: String): Result<Boolean> =
        runCatching {
            userService.signup(
                SignUpRequest(
                    id = id,
                    password = password,
                    extraUserInfo = "extraUserInfo",
                    name = username,
                    profileFilePath = "profileFilePath"
                )
            ).result == "SUCCESS"
        }

}