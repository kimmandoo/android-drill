package com.lawgicalai.bubbychat.data.di

import android.content.Context
import androidx.credentials.CredentialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object CredentialManagerModule {
    @Provides
    @ActivityScoped
    fun provideCredentialManager(
        @ActivityContext context: Context,
    ): CredentialManager = CredentialManager.create(context)
}
