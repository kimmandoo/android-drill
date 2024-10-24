package com.kimmandoo.data.usecase.setting

import com.kimmandoo.data.ktor.UserService
import com.kimmandoo.data.model.toUser
import com.kimmandoo.domain.model.User
import com.kimmandoo.domain.usecase.main.setting.GetMyUserUseCase
import javax.inject.Inject

class GetMyUserUseCaseImpl @Inject constructor(private val userService: UserService) :
    GetMyUserUseCase {
    override suspend fun invoke(): Result<User> = runCatching {
        val response = userService.getUserInfo()
        if (response.result == "SUCCESS") {
            response.data!!.toUser()
        } else {
            throw Exception(response.errorMessage)
        }
    }
}