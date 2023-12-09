package com.example.alertofevents.data.cache.entity

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * The entity reflecting the settings
 */
data class CacheSettings(
    val firstTimeToStart: String,
    val firstTimeToStartEnabled: Boolean,
    val timeForStopAlerting: String,
    val timeForStopAlertingEnabled: Boolean,
    val beforeOnsetTime: String,
    val beforeOnsetTimeEnabled: Boolean,
    val soundName: String
) {
    companion object {
        val FIRST_TIME_TO_START_KEY = stringPreferencesKey("FIRST_TIME_TO_START_KEY")
        val FIRST_TIME_TO_START_ENABLED_KEY = booleanPreferencesKey("FIRST_TIME_TO_START_ENABLED_KEY")
        val BEFORE_ONSET_TIME_KEY = stringPreferencesKey("BEFORE_ONSET_TIME_KEY")
        val BEFORE_ONSET_TIME_ENABLED_KEY = booleanPreferencesKey("BEFORE_ONSET_TIME_ENABLED_KEY")
        val TIME_FOR_STOP_ALERTING_KEY = stringPreferencesKey("TIME_FOR_STOP_ALERTING_KEY")
        val TIME_FOR_STOP_ALERTING_ENABLED_KEY = booleanPreferencesKey("TIME_FOR_STOP_ALERTING_ENABLED_KEY")
        val SOUND_NAME_KEY = stringPreferencesKey("SOUND_NAME_KEY")
    }
}