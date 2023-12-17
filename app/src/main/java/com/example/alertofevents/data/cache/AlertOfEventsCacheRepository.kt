package com.example.alertofevents.data.cache

import com.example.alertofevents.domain.model.Settings
import kotlinx.coroutines.flow.Flow

/**
 * A repository for working with the cache
 */
interface AlertOfEventsCacheRepository {

    /**
     * Used to get settings as Flow
     */
    fun getSettingsFlow(): Flow<Settings>

    /**
     * Used to save settings
     */
    suspend fun saveSettings(settings: Settings)

    /**
     * Used to get the settings
     */
    suspend fun getSettings(): Settings
}