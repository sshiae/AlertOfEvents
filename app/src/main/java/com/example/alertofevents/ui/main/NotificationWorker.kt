package com.example.alertofevents.ui.main

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.alertofevents.R
import com.example.alertofevents.domain.interactor.AlertOfEventsInteractor
import com.example.alertofevents.domain.model.Event
import com.example.alertofevents.domain.model.Settings
import com.example.alertofevents.ui.notification.NotificationActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit


@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    val interactor: AlertOfEventsInteractor
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val settings: Settings = interactor.getSettings()
        val firstTimeToStart: LocalTime = settings.firstTimeToStart
        val minutesForStart: Long = ((firstTimeToStart.hour * 60) + firstTimeToStart.minute).toLong()
        val currentDate = LocalDateTime.now()
        val startDate: LocalDateTime = currentDate.plusMinutes(minutesForStart - INACCURACY_MINUTES)
        val endDate: LocalDateTime = currentDate.plusMinutes(minutesForStart + INACCURACY_MINUTES)
        val foundEvent = interactor.getEventByBetween(startDate, endDate)
        if (foundEvent != null && foundEvent.remindMe) {
            launchNotification(foundEvent)
        }
        return Result.success()
    }

    private fun launchNotification(event: Event) {
        showNotification(event)
        startNotificationActivity(event)
    }

    private fun showNotification(event: Event) {
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(event.name)
            .setContentText(CONTENT_TEXT_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_alarm_black_24dp)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun startNotificationActivity(event: Event) {
        val intent = NotificationActivity.getCallingIntent(applicationContext, event.id ?: DEFAULT_EVENT_ID)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }

    companion object {
        private const val WORK_NAME = "NotificationWorker"

        const val CHANNEL_ID = "1"
        const val CHANNEL_NAME = "Notification"
        const val INACCURACY_MINUTES = 1L
        const val DEFAULT_EVENT_ID = -1L
        const val NOTIFICATION_ID = 1
        const val CONTENT_TEXT_NOTIFICATION = "Ring Ring .. Ring Ring"

        fun scheduleWork(
            applicationContext: Context,
            repeatInterval: Long,
            repeatIntervalTimeUnit: TimeUnit
        ) {
            val periodicWork = PeriodicWorkRequestBuilder<NotificationWorker>(
                repeatInterval,
                repeatIntervalTimeUnit)
                .build()
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                periodicWork)
        }
    }
}