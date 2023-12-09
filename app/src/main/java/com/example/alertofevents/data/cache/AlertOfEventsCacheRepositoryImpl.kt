package com.example.alertofevents.data.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.alertofevents.data.cache.entity.CacheSettings
import com.example.alertofevents.data.cache.entity.CacheSettings.Companion.BEFORE_ONSET_TIME_ENABLED_KEY
import com.example.alertofevents.data.cache.entity.CacheSettings.Companion.BEFORE_ONSET_TIME_KEY
import com.example.alertofevents.data.cache.entity.CacheSettings.Companion.FIRST_TIME_TO_START_ENABLED_KEY
import com.example.alertofevents.data.cache.entity.CacheSettings.Companion.FIRST_TIME_TO_START_KEY
import com.example.alertofevents.data.cache.entity.CacheSettings.Companion.SOUND_NAME_KEY
import com.example.alertofevents.data.cache.entity.CacheSettings.Companion.TIME_FOR_STOP_ALERTING_ENABLED_KEY
import com.example.alertofevents.data.cache.entity.CacheSettings.Companion.TIME_FOR_STOP_ALERTING_KEY
import com.example.alertofevents.data.cache.mapper.toEntity
import com.example.alertofevents.data.cache.mapper.toModel
import com.example.alertofevents.domain.model.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [AlertOfEventsCacheRepository]
 */
class AlertOfEventsCacheRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AlertOfEventsCacheRepository {

    override suspend fun saveSettings(settings: Settings) {
        val mappedSettings: CacheSettings = settings.toEntity()
        dataStore.edit { prefs ->
            prefs[FIRST_TIME_TO_START_KEY] = mappedSettings.firstTimeToStart
            prefs[FIRST_TIME_TO_START_ENABLED_KEY] = mappedSettings.firstTimeToStartEnabled
            prefs[TIME_FOR_STOP_ALERTING_KEY] = mappedSettings.timeForStopAlerting
            prefs[TIME_FOR_STOP_ALERTING_ENABLED_KEY] = mappedSettings.timeForStopAlertingEnabled
            prefs[BEFORE_ONSET_TIME_KEY] = mappedSettings.beforeOnsetTime
            prefs[BEFORE_ONSET_TIME_ENABLED_KEY] = mappedSettings.beforeOnsetTimeEnabled
            prefs[SOUND_NAME_KEY] = mappedSettings.soundName
        }
    }

    override suspend fun getSettings(): Settings {
        val cacheSettings = dataStore.data.map {
            CacheSettings(
                firstTimeToStart = it[FIRST_TIME_TO_START_KEY] ?: DEFAULT_FIRST_TIME_TO_START,
                firstTimeToStartEnabled = it[FIRST_TIME_TO_START_ENABLED_KEY] ?: false,
                timeForStopAlerting = it[TIME_FOR_STOP_ALERTING_KEY] ?: DEFAULT_TIME_FOR_STOP_ALERTING,
                timeForStopAlertingEnabled = it[TIME_FOR_STOP_ALERTING_ENABLED_KEY] ?: false,
                beforeOnsetTime = it[BEFORE_ONSET_TIME_KEY] ?: DEFAULT_BEFORE_ONSET_TIME,
                beforeOnsetTimeEnabled = it[BEFORE_ONSET_TIME_ENABLED_KEY] ?: false,
                soundName = it[SOUND_NAME_KEY] ?: DEFAULT_SOUND_NAME
            )
        }.first()
        return cacheSettings.toModel()
    }

    companion object {
        private const val DEFAULT_FIRST_TIME_TO_START = "00:30"
        private const val DEFAULT_TIME_FOR_STOP_ALERTING = "00:05"
        private const val DEFAULT_BEFORE_ONSET_TIME = "00:05"
        private const val DEFAULT_SOUND_NAME = "Alarm"
    }
}