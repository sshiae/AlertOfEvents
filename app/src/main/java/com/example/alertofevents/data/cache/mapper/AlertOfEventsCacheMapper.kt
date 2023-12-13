package com.example.alertofevents.data.cache.mapper

import com.example.alertofevents.data.cache.entity.CacheSettings
import com.example.alertofevents.domain.model.Settings
import java.time.LocalTime

fun Settings.toEntity(): CacheSettings {
    return CacheSettings(
        firstTimeToStart = firstTimeToStart.toString(),
        timeForStopAlerting = timeForStopAlerting.toString(),
        timeForStopAlertingEnabled = timeForStopAlertingEnabled,
        soundName = soundName
    )
}

fun CacheSettings.toModel(): Settings {
    return Settings(
        firstTimeToStart = LocalTime.parse(firstTimeToStart),
        timeForStopAlerting = LocalTime.parse(timeForStopAlerting),
        timeForStopAlertingEnabled = timeForStopAlertingEnabled,
        soundName = soundName
    )
}