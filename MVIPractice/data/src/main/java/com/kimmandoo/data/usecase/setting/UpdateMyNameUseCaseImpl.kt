package com.kimmandoo.data.usecase.setting

import com.kimmandoo.data.ktor.UserService
import com.kimmandoo.data.model.UpdateMyInfoRequest
import com.kimmandoo.domain.usecase.main.setting.GetMyUserUseCase
import com.kimmandoo.domain.usecase.main.setting.UpdateMyNameUseCase
import javax.inject.Inject

class UpdateMyNameUseCaseImpl @Inject constructor(
    private val userService: UserService,
    private val getMyUserUseCase: GetMyUserUseCase,
) :
    UpdateMyNameUseCase {
    override suspend fun invoke(
        username: String,
    ): Result<Unit> = runCatching {
        val user = getMyUserUseCase().getOrThrow()
        val request = UpdateMyInfoRequest(
            userName = username,
            extraUserInfo = " ",
            profileFilePath = user.profileImageUrl.orEmpty()
        )
        userService.patchUserInfo(request)
    }
}