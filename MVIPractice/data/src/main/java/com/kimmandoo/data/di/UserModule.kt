package com.kimmandoo.data.di

import com.kimmandoo.data.usecase.LoginUseCaseImpl
import com.kimmandoo.domain.usecase.login.LoginUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {
    @Binds
    abstract fun bindLoginUseCase(
        usecase: LoginUseCaseImpl
    ): LoginUseCase
}