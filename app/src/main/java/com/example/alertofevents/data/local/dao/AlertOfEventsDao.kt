package com.example.alertofevents.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.alertofevents.data.local.entity.DatabaseEvent
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface AlertOfEventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateEvent(event: DatabaseEvent)

    @Delete
    suspend fun deleteEvent(event: DatabaseEvent)

    @Query("""
        SELECT   *
        FROM     event_table
        WHERE    id = :id
    """)
    suspend fun getEventById(id: Long): DatabaseEvent

    @Query("""
        SELECT   
            EXISTS (
                SELECT  NULL
                FROM    event_table 
                WHERE   id = :id
            )
    """)
    suspend fun existsEventById(id: Long): Boolean

    @Query("""
        SELECT   *
        FROM     event_table
        WHERE    strftime('%Y-%m-%d', date) = strftime('%Y-%m-%d', :day)
    """)
    suspend fun getEventsByDay(day: LocalDate): List<DatabaseEvent>

    @Query("""
        SELECT   *
        FROM     event_table
        WHERE    date(date) BETWEEN date(:startDate) AND date(:endDate)
        ORDER BY date(date) DESC
        LIMIT    1
    """)
    suspend fun getEventByBetween(startDate: LocalDateTime, endDate: LocalDateTime): DatabaseEvent?

    @Query("""
        SELECT   *
        FROM     event_table
        WHERE    strftime('%Y-%m', date) = strftime('%Y-%m', :month)
    """)
    fun getEventsByMonth(month: LocalDate): Flow<List<DatabaseEvent>>
}