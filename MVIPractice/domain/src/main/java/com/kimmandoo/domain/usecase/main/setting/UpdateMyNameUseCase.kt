package com.kimmandoo.domain.usecase.main.setting

interface UpdateMyNameUseCase {
    suspend operator fun invoke(
        username: String,
    ): Result<Unit>
}