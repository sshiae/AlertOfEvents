package com.example.alertofevents.base.ui

/**
 * Base UI state with the original entity [original]
 */
abstract class BaseUiState<T> {

    /**
     * The original entity on which [BaseUiState] is built.
     */
    abstract val original: T
}