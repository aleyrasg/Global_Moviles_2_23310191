package com.example.global_moviles_2_23310191.ui.reminders

data class RoutineReminder(
    val scheduleId: Int,
    val routineId: String,
    val routineName: String,
    val dayOfWeek: Int,
    val hour: Int,
    val minute: Int
)
