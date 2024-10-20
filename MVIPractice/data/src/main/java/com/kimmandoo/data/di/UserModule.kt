package com.kimmandoo.data.di

import com.kimmandoo.data.usecase.ClearTokenUseCaseImpl
import com.kimmandoo.data.usecase.GetMyUserUseCaseImpl
import com.kimmandoo.data.usecase.GetTokenUseCaseImpl
import com.kimmandoo.data.usecase.LoginUseCaseImpl
import com.kimmandoo.data.usecase.SetTokenUseCaseImpl
import com.kimmandoo.data.usecase.SignUpUseCaseImpl
import com.kimmandoo.domain.usecase.login.ClearTokenUseCase
import com.kimmandoo.domain.usecase.login.GetTokenUseCase
import com.kimmandoo.domain.usecase.login.LoginUseCase
import com.kimmandoo.domain.usecase.login.SetTokenUseCase
import com.kimmandoo.domain.usecase.login.SignUpUseCase
import com.kimmandoo.domain.usecase.main.setting.GetMyUserUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {
    @Binds
    abstract fun bindLoginUseCase(
        usecase: LoginUseCaseImpl,
    ): LoginUseCase

    @Binds
    abstract fun bindSignUpUseCase(
        usecase: SignUpUseCaseImpl,
    ): SignUpUseCase

    @Binds
    abstract fun bindGetTokenUseCase(
        usecase: GetTokenUseCaseImpl,
    ): GetTokenUseCase

    @Binds
    abstract fun bindSetTokenUseCase(
        usecase: SetTokenUseCaseImpl,
    ): SetTokenUseCase

    @Binds
    abstract fun bindClearTokenUseCase(
        usecase: ClearTokenUseCaseImpl,
    ): ClearTokenUseCase

    @Binds
    abstract fun bindGetMyUserUseCase(
        usecase: GetMyUserUseCaseImpl
    ): GetMyUserUseCase

}