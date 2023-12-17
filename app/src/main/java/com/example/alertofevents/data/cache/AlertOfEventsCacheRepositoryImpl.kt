package com.example.alertofevents.data.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.alertofevents.data.cache.entity.CacheSettings
import com.example.alertofevents.data.cache.mapper.toEntity
import com.example.alertofevents.data.cache.mapper.toModel
import com.example.alertofevents.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [AlertOfEventsCacheRepository]
 */
class AlertOfEventsCacheRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AlertOfEventsCacheRepository {

    override fun getSettingsFlow(): Flow<Settings> {
        return dataStore.data.map {
            CacheSettings(
                firstTimeToStart =
                it[FIRST_TIME_TO_START_KEY] ?: DEFAULT_FIRST_TIME_TO_START,
                timeForStopAlerting =
                it[TIME_FOR_STOP_ALERTING_KEY] ?: DEFAULT_TIME_FOR_STOP_ALERTING,
                timeForStopAlertingEnabled =
                it[TIME_FOR_STOP_ALERTING_ENABLED_KEY] ?: false,
                soundName =
                it[SOUND_NAME_KEY] ?: DEFAULT_SOUND_NAME
            )
        }.map {
            it.toModel()
        }
    }

    override suspend fun getSettings(): Settings {
        return dataStore.data.map {
            CacheSettings(
                firstTimeToStart =
                it[FIRST_TIME_TO_START_KEY] ?: DEFAULT_FIRST_TIME_TO_START,
                timeForStopAlerting =
                it[TIME_FOR_STOP_ALERTING_KEY] ?: DEFAULT_TIME_FOR_STOP_ALERTING,
                timeForStopAlertingEnabled =
                it[TIME_FOR_STOP_ALERTING_ENABLED_KEY] ?: false,
                soundName =
                it[SOUND_NAME_KEY] ?: DEFAULT_SOUND_NAME
            )
        }.map {
            it.toModel()
        }.first()
    }

    override suspend fun saveSettings(settings: Settings) {
        val mappedSettings: CacheSettings = settings.toEntity()
        dataStore.edit { prefs ->
            prefs[FIRST_TIME_TO_START_KEY] = mappedSettings.firstTimeToStart
            prefs[TIME_FOR_STOP_ALERTING_KEY] = mappedSettings.timeForStopAlerting
            prefs[TIME_FOR_STOP_ALERTING_ENABLED_KEY] = mappedSettings.timeForStopAlertingEnabled
            prefs[SOUND_NAME_KEY] = mappedSettings.soundName
        }
    }

    companion object {
        private val FIRST_TIME_TO_START_KEY =
            stringPreferencesKey("FIRST_TIME_TO_START_KEY")
        private val TIME_FOR_STOP_ALERTING_KEY =
            stringPreferencesKey("TIME_FOR_STOP_ALERTING_KEY")
        private val TIME_FOR_STOP_ALERTING_ENABLED_KEY =
            booleanPreferencesKey("TIME_FOR_STOP_ALERTING_ENABLED_KEY")
        private val SOUND_NAME_KEY =
            stringPreferencesKey("SOUND_NAME_KEY")

        private const val DEFAULT_FIRST_TIME_TO_START = "00:15"
        private const val DEFAULT_TIME_FOR_STOP_ALERTING = "00:15"
        private const val DEFAULT_SOUND_NAME = "Alarm"
    }
}