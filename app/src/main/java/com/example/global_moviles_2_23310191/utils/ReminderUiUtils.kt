package com.example.global_moviles_2_23310191.utils

import java.util.Locale

private val dayLabels = mapOf(
    1 to "Dom",
    2 to "Lun",
    3 to "Mar",
    4 to "Mie",
    5 to "Jue",
    6 to "Vie",
    7 to "Sab"
)

fun formatReminderTime(hour: Int, minute: Int): String {
    if (hour < 0 || minute < 0) return "--:--"
    return String.format(Locale.US, "%02d:%02d", hour, minute)
}

fun formatReminderDays(days: List<Int>): String {
    if (days.isEmpty()) return "Sin dias"
    return days.sorted().joinToString(" ") { dayLabels[it] ?: it.toString() }
}

