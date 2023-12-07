package com.example.alertofevents.ui.notification

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alertofevents.databinding.NotificationActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: NotificationActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NotificationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        animateClock()
        //TODO: На кнопку поставить событие остановки сервиса
    }

    private fun animateClock() {
        with(binding) {
            val rotateAnimation = ObjectAnimator.ofFloat(ivRingClock, "rotation", 0f, 20f, 0f, -20f, 0f)
            rotateAnimation.repeatCount = ValueAnimator.INFINITE
            rotateAnimation.duration = 800
            rotateAnimation.start()
        }
    }
}