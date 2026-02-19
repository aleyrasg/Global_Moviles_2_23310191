package com.example.global_moviles_2_23310191.data.model

data class Routine(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "",          // push/pull/pierna/cardio
    val durationMin: Int = 0,
    val notes: String = "",
    val audioUrl: String? = null,
    // Dias de la semana: 1=Dom, 2=Lun, ... 7=Sab
    val reminderDays: List<Int> = emptyList(),
    // Hora del recordatorio (24h). -1 significa sin recordatorio.
    val reminderHour: Int = -1,
    val reminderMinute: Int = -1,
    val createdAt: Long = System.currentTimeMillis()
)
