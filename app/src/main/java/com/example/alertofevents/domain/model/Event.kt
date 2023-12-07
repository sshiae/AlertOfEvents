package com.example.alertofevents.domain.model

import java.time.LocalDateTime

/**
 * The model reflecting the event
 */
data class Event(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val date: LocalDateTime = LocalDateTime.now(),
    val remindMe: Boolean = false
)