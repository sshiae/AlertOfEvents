package com.example.alertofevents.di

import com.example.alertofevents.data.cache.AlertOfEventsCacheRepository
import com.example.alertofevents.data.cache.AlertOfEventsCacheRepositoryImpl
import com.example.alertofevents.data.local.AlertOfEventsLocalRepository
import com.example.alertofevents.data.local.AlertOfEventsLocalRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @[Binds Singleton]
    abstract fun bindAlertOfEventsLocalRepository(
        alertOfEventsLocalRepositoryImpl: AlertOfEventsLocalRepositoryImpl
    ): AlertOfEventsLocalRepository

    @[Binds Singleton]
    abstract fun bindAlertOfEventsCacheRepository(
        alertOfEventsCacheRepositoryImpl: AlertOfEventsCacheRepositoryImpl
    ): AlertOfEventsCacheRepository
}