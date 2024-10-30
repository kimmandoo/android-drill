package com.kimmandoo.data.di

import com.kimmandoo.data.usecase.writing.GetLocalImageListUseCaseImpl
import com.kimmandoo.domain.usecase.writing.GetLocalImageListUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class) // 액티비티와 생명주기를 함께함
abstract class WritingModule {

    @Binds
    abstract fun bindGetLocalImageListUseCase(uc: GetLocalImageListUseCaseImpl): GetLocalImageListUseCase
}