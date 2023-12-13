package com.example.alertofevents.data.cache

import com.example.alertofevents.domain.model.Settings

/**
 * A repository for working with the cache
 */
interface AlertOfEventsCacheRepository {

    /**
     * Used to save settings
     */
    suspend fun saveSettings(settings: Settings)

    /**
     * Used to get the settings
     */
    suspend fun getSettings(): Settings

    /**
     * To get an indication whether the notification service has been started
     */
    suspend fun isWorkerScheduled(): Boolean

    /**
     * to set the indicator of the neglect of the notification service
     */
    suspend fun setWorkerScheduled(workerScheduled: Boolean)
}