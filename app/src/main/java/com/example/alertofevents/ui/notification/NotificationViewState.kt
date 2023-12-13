package com.example.alertofevents.ui.notification

import com.example.alertofevents.domain.model.Event
import com.example.alertofevents.domain.model.Settings

/**
 * View state for NotificationViewModel
 */
data class NotificationViewState(
    val event: Event,
    val settings: Settings
)