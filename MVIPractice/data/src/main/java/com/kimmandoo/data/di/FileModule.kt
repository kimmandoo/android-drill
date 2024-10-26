package com.kimmandoo.data.di

import com.kimmandoo.data.ktor.ImageService
import com.kimmandoo.data.usecase.file.GetImageUseCaseImpl
import com.kimmandoo.data.usecase.file.GetInputStreamUseCaseImpl
import com.kimmandoo.data.usecase.file.UploadImageUseCaseImpl
import com.kimmandoo.data.usecase.setting.SetProfileImageUseCaseImpl
import com.kimmandoo.domain.usecase.file.GetImageUseCase
import com.kimmandoo.domain.usecase.file.GetInputStreamUseCase
import com.kimmandoo.domain.usecase.file.UploadImageUseCase
import com.kimmandoo.domain.usecase.main.setting.SetProfileImageUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FileModule {
    @Binds
    abstract fun bindUploadImageUseCase(uc: UploadImageUseCaseImpl): UploadImageUseCase

    @Binds
    abstract fun bindSetProfileImageUseCase(uc: SetProfileImageUseCaseImpl): SetProfileImageUseCase

    @Binds
    abstract fun bindGetInputStreamUseCase(uc: GetInputStreamUseCaseImpl): GetInputStreamUseCase

    @Binds
    abstract fun bindGetImageUseCase(uc: GetImageUseCaseImpl): GetImageUseCase
}