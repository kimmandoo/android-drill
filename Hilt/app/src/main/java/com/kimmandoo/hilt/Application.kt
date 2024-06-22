package com.kimmandoo.hilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class Application: Application() {

    override fun onCreate() {
        super.onCreate()

    }
}