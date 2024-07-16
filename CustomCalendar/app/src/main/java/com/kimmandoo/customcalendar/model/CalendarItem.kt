package com.kimmandoo.customcalendar.model

import java.time.LocalDate

data class CalendarItem(
    val date: LocalDate,
    val isSunday: Boolean,
    val isCurrentMonth: Boolean
)
