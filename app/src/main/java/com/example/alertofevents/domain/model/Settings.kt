package com.example.alertofevents.domain.model

import java.time.LocalTime

/**
 * The model reflecting the settings
 */
data class Settings(
    val firstTimeToStart: LocalTime,
    val beforeOnsetTime: LocalTime,
    val timeForStopAlerting: LocalTime,
    val soundName: String
)