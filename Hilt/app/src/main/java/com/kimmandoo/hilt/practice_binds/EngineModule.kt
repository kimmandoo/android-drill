package com.kimmandoo.hilt.practice_binds

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
// Binds를 위한 추상 모듈을 만들어 준다
abstract class EngineModule {
//    @Binds
//    abstract fun bindGasolineEngine(engine: GasolineEngine): Engine

    @Binds
    abstract fun bindDieselEngine(engine: DieselEngine): Engine
}