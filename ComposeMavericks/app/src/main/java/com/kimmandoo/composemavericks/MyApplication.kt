package com.kimmandoo.composemavericks

import android.app.Application
import com.airbnb.mvrx.Mavericks

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Mavericks 초기화
        Mavericks.initialize(this)
    }
}