package com.kimmandoo.presentation.main.writing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kimmandoo.presentation.theme.MVIPracticeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WritingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVIPracticeTheme {
                WritingNavHost(onFinish = { finish() })
            }
        }
    }
}