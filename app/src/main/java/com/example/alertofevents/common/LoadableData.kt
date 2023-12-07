package com.example.alertofevents.common

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Interface for wrapping loaded data.
 */
sealed interface LoadableData<T> {

    /**
     * Data of the current state.
     */
    val value: T?

    /**
     * Whether data of the current state is available.
     */
    val hasValue: Boolean
        get() = (value != null)

    /**
     * Loading state.
     *
     * @param value - data of the last status
     */
    data class Loading<T>(
        override val value: T? = null
    ) : LoadableData<T>

    /**
     * State of successful data loading [value].
     */
    data class Success<T>(
        override val value: T
    ) : LoadableData<T>

    /**
     * State of unsuccessful data loading with an [error].
     *
     * @param value - data of the last status
     */
    data class Error<T>(
        override val value: T? = null,
        val error: Exception
    ) : LoadableData<T>
}

/**
 * Emits a loading state with optional data [data].
 */
fun <T> MutableStateFlow<LoadableData<T>>.emitLoading(data: T? = null) {
    value = LoadableData.Loading(data)
}

/**
 * Emits a successful loading state with data [data].
 */
fun <T> MutableStateFlow<LoadableData<T>>.emitSuccess(data: T) {
    value = LoadableData.Success(data)
}

/**
 * Emits an unsuccessful loading state with optional data [data] and an error [error].
 */
fun <T> MutableStateFlow<LoadableData<T>>.emitError(error: Exception, data: T? = null) {
    value = LoadableData.Error(data, error)
}