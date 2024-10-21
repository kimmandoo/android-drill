package com.kimmandoo.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kimmandoo.domain.usecase.login.GetTokenUseCase
import com.kimmandoo.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SplashActivity"
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    @Inject
    lateinit var getTokenUseCase: GetTokenUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            Log.d(TAG, "onCreate: ${getTokenUseCase()}")
            val isLoggedIn = !getTokenUseCase().isNullOrEmpty()
            Log.d(TAG, "onCreate: $isLoggedIn")
            if (isLoggedIn) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }
}