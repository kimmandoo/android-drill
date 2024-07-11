package com.kimmandoo.swipegesture360

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    private lateinit var ticketView: ImageView
    private var rotationY = 0f
    private lateinit var gestureDetector: GestureDetectorCompat

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ticketView = findViewById(R.id.ticketView)
        gestureDetector = GestureDetectorCompat(this, TicketGestureListener())

        ticketView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    inner class TicketGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            // distanceX는 이전 이벤트와 현재 이벤트 사이의 X축 거리
            // 부드러운 회전을 위해 작은 값으로 나눕니다
            val rotation = -distanceX / 5 // 음수를 사용하여 스와이프 방향과 회전 방향을 일치시킵니다

            rotationY += rotation
            rotationY %= 360f // 360도를 넘어가면 다시 0부터 시작

            ticketView.rotationY = rotationY

            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val distanceX = e2.x - e1!!.x
            val rotation = distanceX / 5 // 회전 속도 조절

            rotationY += rotation
            rotationY %= 360f // 360도를 넘어가면 다시 0부터 시작

            ObjectAnimator.ofFloat(ticketView, "rotationY", rotationY).apply {
                duration = 300
                start()
            }

            return true
        }
    }
}