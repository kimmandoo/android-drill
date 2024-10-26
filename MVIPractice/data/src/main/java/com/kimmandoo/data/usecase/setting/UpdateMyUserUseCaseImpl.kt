package com.kimmandoo.data.usecase.setting

import com.kimmandoo.data.ktor.UserService
import com.kimmandoo.data.model.UpdateMyInfoRequest
import com.kimmandoo.domain.usecase.main.setting.GetMyUserUseCase
import com.kimmandoo.domain.usecase.main.setting.UpdateMyUserUseCase
import javax.inject.Inject

class UpdateMyUserUseCaseImpl @Inject constructor(
    private val userService: UserService,
    private val getMyUserUseCase: GetMyUserUseCase,
) : UpdateMyUserUseCase {
    override suspend fun invoke(
        username: String?,
        profileImageUrl: String?
    ): Result<Unit> = runCatching {
        val user = getMyUserUseCase().getOrThrow()
        val request = UpdateMyInfoRequest(
            userName = username?: user.username,
            extraUserInfo = " ",
            profileFilePath = profileImageUrl ?: user.profileImageUrl.orEmpty()
        )
        userService.patchUserInfo(request)
    }
}