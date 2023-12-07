package com.example.alertofevents.data.local.mapper

import com.example.alertofevents.data.local.entity.DatabaseEvent
import com.example.alertofevents.domain.model.Event

fun List<DatabaseEvent>.toModels(): List<Event> {
    return map { it.toModel() }
}

fun DatabaseEvent.toModel(): Event {
    return Event(
        id = id,
        name = name,
        description = description,
        date = date,
        remindMe = remindMe
    )
}

fun Event.toEntity(): DatabaseEvent {
    return DatabaseEvent(
        id = id,
        name = name!!,
        description = description!!,
        date = date,
        remindMe = remindMe
    )
}