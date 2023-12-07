package com.example.alertofevents.ui.calendarEvents.uiEvent

/**
 * UI events for the fragment [CalendarEventsFragment]
 */
sealed interface CalendarEventsUiEvent {

    /**
     * Open a fragment with calendar editing
     */
    data class OpenEventForEdit(
        val eventId: Long
    ) : CalendarEventsUiEvent
}