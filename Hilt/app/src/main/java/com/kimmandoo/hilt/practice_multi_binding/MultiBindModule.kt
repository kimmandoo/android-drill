package com.kimmandoo.hilt.practice_multi_binding

import com.kimmandoo.hilt.Test
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet


@Module
@InstallIn(SingletonComponent::class)
class MultiBindModule {
    @Provides
    @IntoSet
    fun provideString(): String {
        return "test"
    }

    @Provides
    @ElementsIntoSet
    fun provideStrings(): Set<String> {
        return setOf("a", "b", "c")
    }

    @Provides
    @IntoMap @IntKey(1)
    fun provideName(): String{
        return "one"
    }

    @Provides
    @IntoMap @ClassKey(Test::class)
    fun provideNameOfClass(): String{
        return "Test Value"
    }

    @Provides
    @IntoMap
    @CustomMapKey(Keys.ONE)
    fun provideCK(): String{
        return "CK"
    }
}