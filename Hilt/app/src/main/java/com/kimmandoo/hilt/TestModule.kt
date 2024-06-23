package com.kimmandoo.hilt

import com.kimmandoo.hilt.practice_binding.TestQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestModule {

//    @TestQualifier
    @Named("Test")
    @Provides
    fun provideTest2(): Test {
        return Test(id = "TestQualifier")
    }
}