package com.kimmandoo.domain.usecase.main.setting

import com.kimmandoo.domain.model.User

interface GetMyUserUseCase{
    suspend operator fun invoke(): Result<User>
}
