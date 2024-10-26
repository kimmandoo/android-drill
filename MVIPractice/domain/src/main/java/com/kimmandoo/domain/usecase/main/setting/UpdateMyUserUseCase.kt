package com.kimmandoo.domain.usecase.main.setting

interface UpdateMyUserUseCase {
    suspend operator fun invoke(
        username: String? = null,
        profileImageUrl: String? = null
    ): Result<Unit>
}