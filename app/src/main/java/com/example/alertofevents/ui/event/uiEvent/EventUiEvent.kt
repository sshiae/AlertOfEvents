package com.example.alertofevents.ui.event.uiEvent

/**
 * UI events for the fragment [EventFragment]
 */
sealed interface EventUiEvent {

    /**
     * Open the calendar of events with [date]
     */
    data object OpenCalendarOfEventsFragment: EventUiEvent

    /**
     * Used to clear all fields
     */
    data object ClearFields : EventUiEvent
}