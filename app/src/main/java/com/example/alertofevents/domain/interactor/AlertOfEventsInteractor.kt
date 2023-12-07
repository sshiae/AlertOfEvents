package com.example.alertofevents.domain.interactor

import com.example.alertofevents.data.cache.AlertOfEventsCacheRepository
import com.example.alertofevents.data.local.AlertOfEventsLocalRepository
import com.example.alertofevents.domain.model.Event
import com.example.alertofevents.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import javax.inject.Inject

/**
 * Interactor for interacting with various repositories
 */
class AlertOfEventsInteractor @Inject constructor(
    private val localRepository: AlertOfEventsLocalRepository,
    private val cacheRepository: AlertOfEventsCacheRepository
) {
    suspend fun insertOrUpdateEvent(event: Event) {
        localRepository.insertOrUpdateEvent(event)
    }

    suspend fun deleteEvent(event: Event) {
        localRepository.deleteEvent(event)
    }

    suspend fun getEventById(id: Long): Event {
        return localRepository.getEventById(id)
    }

    suspend fun saveSettings(settings: Settings) {
        cacheRepository.saveSettings(settings)
    }

    suspend fun getSettings(): Settings {
        return cacheRepository.getSettings()
    }

    suspend fun getEventsByDay(day: LocalDate): List<Event> {
        return localRepository.getEventsByDay(day)
    }

    fun getExistenceEventsByDay(month: YearMonth): Flow<Map<Int, Boolean>> {
        return localRepository.getExistenceEventsByDay(month)
    }

    fun getEventsByDate(date: LocalDateTime): Flow<List<Event>> {
        return localRepository.getEventsByDate(date)
    }

    fun getAllEventsAfterCurrentDate(): Flow<List<Event>> {
        return localRepository.getAllEventsAfterCurrentDate()
    }
}