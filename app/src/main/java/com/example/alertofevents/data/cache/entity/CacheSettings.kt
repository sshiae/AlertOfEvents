package com.example.alertofevents.data.cache.entity

/**
 * The entity reflecting the settings
 */
data class CacheSettings(
    val firstTimeToStart: String,
    val timeForStopAlerting: String,
    val timeForStopAlertingEnabled: Boolean,
    val soundName: String
)