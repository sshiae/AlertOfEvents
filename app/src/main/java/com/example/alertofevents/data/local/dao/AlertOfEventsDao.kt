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
import java.time.YearMonth

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
        SELECT   *
        FROM     event_table
        WHERE    strftime('%Y-%m-%d', date) = strftime('%Y-%m-%d', :day)
    """)
    suspend fun getEventsByDay(day: LocalDate): List<DatabaseEvent>

    @Query("""
        SELECT   *
        FROM     event_table
        WHERE    strftime('%Y-%m', date) = strftime('%Y-%m', :month)
    """)
    fun getEventsByMonth(month: LocalDate): Flow<List<DatabaseEvent>>

    @Query("""
        SELECT   *
        FROM     event_table
        WHERE    date(date) = date(:date)
    """)
    fun getEventsByDate(date: LocalDateTime): Flow<List<DatabaseEvent>>

    @Query("""
        SELECT   *
        FROM     event_table
        WHERE    date >= date('now')
    """)
    fun getAllEventsAfterCurrentDate(): Flow<List<DatabaseEvent>>
}