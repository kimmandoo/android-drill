package com.kimmandoo.domain.usecase.login

interface GetTokenUseCase {
    suspend operator fun invoke(): String?
}