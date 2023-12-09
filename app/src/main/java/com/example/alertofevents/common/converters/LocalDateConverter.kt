package com.example.alertofevents.common.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Converter from strings to dates and vise versa
 */
class LocalDateConverter {

    @TypeConverter
    fun fromLocalDateTimeString(value: String?): LocalDateTime? {
        return value?.let {
            return LocalDateTime.parse(value)
        }
    }

    @TypeConverter
    fun localDateTimeToString(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromLocalDateString(value: String?): LocalDate? {
        return value?.let {
            return LocalDate.parse(value)
        }
    }

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? {
        return date?.toString()
    }
}