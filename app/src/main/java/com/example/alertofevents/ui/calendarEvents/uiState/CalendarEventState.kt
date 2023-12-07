package com.example.alertofevents.ui.calendarEvents.uiState

import com.example.alertofevents.base.ui.BaseUiState
import com.example.alertofevents.domain.model.Event

/**
 * Event status for the event list
 */
data class CalendarEventState(
    val name: String,
    val date: String,
    override val original: Event
) : BaseUiState<Event>()