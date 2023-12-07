package com.example.alertofevents.ui.event.uiEvent

import java.time.LocalDateTime

/**
 * UI events for the fragment [EventFragment]
 */
sealed interface EventUiEvent {

    /**
     * Open the calendar of events with [date]
     */
    data class OpenCalendarOfEventsFragment(
        val date: LocalDateTime = LocalDateTime.now()
    ): EventUiEvent

    /**
     * Used to clear all fields
     */
    data object ClearFields : EventUiEvent
}