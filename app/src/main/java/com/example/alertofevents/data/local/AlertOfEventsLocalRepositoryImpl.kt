package com.example.alertofevents.data.local

import com.example.alertofevents.data.local.dao.AlertOfEventsDao
import com.example.alertofevents.data.local.mapper.toEntity
import com.example.alertofevents.data.local.mapper.toModel
import com.example.alertofevents.data.local.mapper.toModels
import com.example.alertofevents.domain.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import javax.inject.Inject

/**
 * Implementation of [AlertOfEventsLocalRepository]
 */
class AlertOfEventsLocalRepositoryImpl @Inject constructor(
    private val alertOfEventsDao: AlertOfEventsDao
) : AlertOfEventsLocalRepository {

    override suspend fun insertOrUpdateEvent(event: Event) {
        alertOfEventsDao.insertOrUpdateEvent(event.toEntity())
    }

    override suspend fun deleteEvent(event: Event) {
        alertOfEventsDao.deleteEvent(event.toEntity())
    }

    override suspend fun getEventById(id: Long): Event {
        return alertOfEventsDao.getEventById(id).toModel()
    }

    override suspend fun getEventsByDay(day: LocalDate): List<Event> {
        return alertOfEventsDao.getEventsByDay(day).map { it.toModel() }
    }

    override suspend fun getEventByBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Event? {
        return alertOfEventsDao.getEventByBetween(startDate, endDate)?.toModel()
    }

    override fun getExistenceEventsByDay(month: YearMonth): Flow<Map<Int, Boolean>> {
        return alertOfEventsDao.getEventsByMonth(month.atDay(1))
            .map {
                val mapOfExistenceEvents = mutableMapOf<Int, Boolean>()
                for (event in it) {
                    mapOfExistenceEvents[event.date.dayOfMonth] = true
                }
                mapOfExistenceEvents
            }
    }
}