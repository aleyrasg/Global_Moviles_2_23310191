package com.example.global_moviles_2_23310191.ui.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.global_moviles_2_23310191.notifications.ReminderScheduler
import com.example.global_moviles_2_23310191.utils.nextWeeklyTriggerMillis

class RoutineReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getIntExtra("scheduleId", -1)
        val routineId = intent.getStringExtra("routineId") ?: return
        val routineName = intent.getStringExtra("routineName") ?: "Rutina"
        val dayOfWeek = intent.getIntExtra("dayOfWeek", -1)
        val hour = intent.getIntExtra("hour", -1)
        val minute = intent.getIntExtra("minute", -1)

        if (scheduleId == -1 || dayOfWeek == -1 || hour == -1 || minute == -1) return

        // ✅ Notificación rápida (para entrega). Luego lo cambiamos a NotificationCompat si quieres
        Toast.makeText(context, "Hora de: $routineName", Toast.LENGTH_LONG).show()

        // ✅ Reprogramar siguiente semana
        val next = nextWeeklyTriggerMillis(dayOfWeek, hour, minute)

        ReminderScheduler.scheduleWeeklyRoutine(
            context = context,
            scheduleId = scheduleId,
            triggerAtMillis = next,
            routineName = routineName,
            routineId = routineId,
            dayOfWeek = dayOfWeek,
            hour = hour,
            minute = minute
        )
    }
}
