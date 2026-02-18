package com.example.global_moviles_2_23310191.utils

import java.util.Calendar

fun nextWeeklyTriggerMillis(dayOfWeek: Int, hour: Int, minute: Int): Long {
    val now = Calendar.getInstance()

    val target = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, dayOfWeek)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    if (target.timeInMillis <= now.timeInMillis) {
        target.add(Calendar.WEEK_OF_YEAR, 1)
    }

    return target.timeInMillis
}
