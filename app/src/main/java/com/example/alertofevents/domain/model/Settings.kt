package com.example.alertofevents.domain.model

import java.time.LocalTime

/**
 * The model reflecting the settings
 */
data class Settings(
    val firstTimeToStart: LocalTime,
    val firstTimeToStartEnabled: Boolean,
    val timeForStopAlerting: LocalTime,
    val timeForStopAlertingEnabled: Boolean,
    val soundName: String
)