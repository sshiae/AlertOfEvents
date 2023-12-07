package com.example.alertofevents.di

import android.content.Context
import androidx.room.Room
import com.example.alertofevents.common.database.AlertOfEventsDatabase
import com.example.alertofevents.data.local.dao.AlertOfEventsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @[Provides Singleton]
    fun provideRoomDatabase(@ApplicationContext context: Context): AlertOfEventsDatabase {
        return Room.databaseBuilder(
            context,
            AlertOfEventsDatabase::class.java,
            AlertOfEventsDatabase.DATABASE_NAME)
            .build()
    }

    @[Provides Singleton]
    fun provideAlertOfEventsDao(database: AlertOfEventsDatabase): AlertOfEventsDao {
        return database.alertOfEventsDao()
    }
}