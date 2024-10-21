package com.kimmandoo.domain.usecase.login

interface SetTokenUseCase {
    suspend operator fun invoke(token: String)
}