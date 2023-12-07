package com.example.alertofevents.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.alertofevents.common.converters.LocalDateConverter
import com.example.alertofevents.common.database.AlertOfEventsDatabase.Companion.DATABASE_VERSION
import com.example.alertofevents.data.local.dao.AlertOfEventsDao
import com.example.alertofevents.data.local.entity.DatabaseEvent

@Database(
    entities = [DatabaseEvent::class],
    version = DATABASE_VERSION
)
@TypeConverters(LocalDateConverter::class)
abstract class AlertOfEventsDatabase : RoomDatabase() {
    abstract fun alertOfEventsDao(): AlertOfEventsDao

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "AlertOfEventsDatabase"
    }
}