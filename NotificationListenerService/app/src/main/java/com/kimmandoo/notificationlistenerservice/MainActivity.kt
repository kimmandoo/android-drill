package com.kimmandoo.notificationlistenerservice

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 부여됨
            println("알림 권한이 부여되었습니다.")
        } else {
            // 권한이 거부됨
            println("알림 권한이 거부되었습니다.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        checkAndRequestNotificationPermission()
        if (!isNotificationPermissionGranted()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        findViewById<TextView>(R.id.btnOnOff).setOnClickListener {
            val notificationHelper = NotificationHelper(baseContext)
            notificationHelper.showNotification("제목", "메시지 내용")
        }
    }

    @SuppressLint("ServiceCast")
    private fun isNotificationPermissionGranted(): Boolean {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationListenerAccessGranted(
            ComponentName(
                application,
                MyNotificationListenerService::class.java
            )
        )
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 이미 권한이 부여됨
                    println("알림 권한이 이미 부여되어 있습니다.")
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    // 권한 요청에 대한 추가 설명이 필요한 경우
                    showPermissionRationaleDialog()
                }
                else -> {
                    // 권한 요청
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android 13 미만 버전에서는 별도의 런타임 권한 요청이 필요 없음
            println("이 기기 버전에서는 별도의 알림 권한 요청이 필요하지 않습니다.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationaleDialog() {
        // 권한이 필요한 이유를 설명하는 다이얼로그를 표시
        // 사용자가 이해하고 수락할 수 있도록 명확하게 설명
        // 예시:
        AlertDialog.Builder(this)
            .setTitle("알림 권한 필요")
            .setMessage("이 앱의 주요 기능을 사용하려면 알림 권한이 필요합니다.")
            .setPositiveButton("권한 요청") { _, _ ->
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}