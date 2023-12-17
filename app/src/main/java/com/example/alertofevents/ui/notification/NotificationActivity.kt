package com.example.alertofevents.ui.notification

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.alertofevents.common.LoadableData
import com.example.alertofevents.databinding.NotificationActivityBinding
import com.example.alertofevents.domain.model.Settings
import com.example.alertofevents.ui.main.NotificationWorker
import com.example.alertofevents.ui.notification.NotificationViewModel.Companion.DEFAULT_EVENT_ID
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.temporal.ChronoUnit


@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null

    private val autoCloseRunnable = Runnable {
        finishNotification()
    }

    private lateinit var binding: NotificationActivityBinding

    private val viewModel: NotificationViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                NotificationViewModel.Factory> { factory ->
                val eventId = intent.getLongExtra(EXTRA_EVENT_ID, DEFAULT_EVENT_ID)
                factory.create(eventId)
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NotificationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        animateClock()
        subscribeToViewModel()
        setupViews()
    }

    override fun onStart() {
        super.onStart()
        viewModel.firstLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlaying()
        handler?.removeCallbacks(autoCloseRunnable)
    }

    private fun setupViews() {
        with(binding) {
            btnOKRingClock.setOnClickListener { finishNotification() }
        }
    }

    private fun finishNotification() {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NotificationWorker.NOTIFICATION_ID)
        finish()
    }

    private fun subscribeToViewModel() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.viewState.collect(::onViewState) }
            }
        }
    }

    private fun onViewState(state: LoadableData<NotificationViewState>) {
        when(state) {
            is LoadableData.Success -> {
                render(state.value)
            }
            is LoadableData.Loading -> {
                // ignored
            }
            is LoadableData.Error -> {
                // ignored
            }
        }
    }

    private fun render(state: NotificationViewState) {
        with(binding) {
            tvTitleEvent.text = state.event.name
            playSound(state.settings.soundName)
            startHandlerForStopTimer(state.settings)
        }
    }

    private fun startHandlerForStopTimer(settings: Settings) {
        if (handler == null && settings.timeForStopAlertingEnabled) {
            val mills: Long = calculateTimeMillis(settings.timeForStopAlerting)
            handler = Handler(Looper.getMainLooper())
            handler?.postDelayed(autoCloseRunnable, mills)
        }
    }

    private fun calculateTimeMillis(targetTime: LocalTime): Long {
        val currentTime = LocalTime.now()
        val secondsUntilTarget = currentTime.until(targetTime, ChronoUnit.SECONDS)
        return secondsUntilTarget * 1000
    }

    private fun animateClock() {
        with(binding) {
            val propertyName = "rotation"
            val rotateAnimation = ObjectAnimator.ofFloat(
                ivRingClock,
                propertyName,
                0f, 20f, 0f, -20f, 0f
            )
            rotateAnimation.repeatCount = ValueAnimator.INFINITE
            rotateAnimation.duration = 800
            rotateAnimation.start()
        }
    }

    private fun playSound(soundName: String) {
        val name = soundName.lowercase().replace(" ", "_")
        val defType = "raw"
        val defPackage = this.packageName
        val soundResourceId = resources.getIdentifier(name, defType, defPackage)
        if (soundResourceId != 0) {
            stopPlaying()
            mediaPlayer = MediaPlayer.create(this, soundResourceId)
            mediaPlayer?.setOnCompletionListener { playSound(soundName) }
            mediaPlayer?.start()
        }
    }

    private fun stopPlaying() {
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    companion object {
        private const val EXTRA_EVENT_ID = "EXTRA_EVENT_TITLE"

        fun getCallingIntent(context: Context, eventId: Long): Intent {
            val intent = Intent(context, NotificationActivity::class.java)
            intent.putExtra(EXTRA_EVENT_ID, eventId)
            return intent
        }
    }
}