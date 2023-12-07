package com.example.alertofevents.data.local

import com.example.alertofevents.domain.model.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

/**
 * Local repository for database access
 */
interface AlertOfEventsLocalRepository {

    /**
     * Insert an [Event] into the database or update [Event]
     */
    suspend fun insertOrUpdateEvent(event: Event)

    /**
     * Used to delete an [event]
     */
    suspend fun deleteEvent(event: Event)

    /**
     * Used to search for an [Event] by [id]
     */
    suspend fun getEventById(id: Long): Event

    /**
     * Used to get an event by [day]
     */
    suspend fun getEventsByDay(day: LocalDate): List<Event>

    /**
     * Get all [Event]s by [date]
     */
    fun getEventsByDate(date: LocalDateTime): Flow<List<Event>>

    /**
     * Get all [Event]s after the current date
     */
    fun getAllEventsAfterCurrentDate(): Flow<List<Event>>

    /**
     * Get the existence of events by day
     */
    fun getExistenceEventsByDay(month: YearMonth): Flow<Map<Int, Boolean>>
}