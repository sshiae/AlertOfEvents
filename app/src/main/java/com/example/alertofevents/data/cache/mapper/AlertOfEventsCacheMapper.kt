package com.example.alertofevents.data.cache.mapper

import com.example.alertofevents.data.cache.entity.CacheSettings
import com.example.alertofevents.domain.model.Settings
import java.time.LocalTime

fun Settings.toEntity(): CacheSettings {
    return CacheSettings(
        firstTimeToStart = firstTimeToStart.toString(),
        beforeOnsetTime = beforeOnsetTime.toString(),
        timeForStopAlerting = timeForStopAlerting.toString(),
        soundName = soundName
    )
}

fun CacheSettings.toModel(): Settings {
    return Settings(
        firstTimeToStart = LocalTime.parse(firstTimeToStart),
        beforeOnsetTime = LocalTime.parse(beforeOnsetTime),
        timeForStopAlerting = LocalTime.parse(timeForStopAlerting),
        soundName = soundName
    )
}