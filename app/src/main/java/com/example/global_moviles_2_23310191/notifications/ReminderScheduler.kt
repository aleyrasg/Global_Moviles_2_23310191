package com.example.global_moviles_2_23310191.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.global_moviles_2_23310191.ui.reminders.RoutineReminderReceiver

object ReminderScheduler {

    fun scheduleWeeklyRoutine(
        context: Context,
        scheduleId: Int,
        triggerAtMillis: Long,
        routineName: String,
        routineId: String,
        dayOfWeek: Int,
        hour: Int,
        minute: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, RoutineReminderReceiver::class.java).apply {
            putExtra("scheduleId", scheduleId)
            putExtra("routineId", routineId)
            putExtra("routineName", routineName)
            putExtra("dayOfWeek", dayOfWeek)
            putExtra("hour", hour)
            putExtra("minute", minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ✅ Android 12+ (API 31): validar permiso para alarmas exactas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val canExact = alarmManager.canScheduleExactAlarms()
            if (!canExact) {
                // Si no puedes, usa una alarma "inexacta" (igual funciona, pero no exacta al segundo)
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                return
            }
        }

        // ✅ Programar exacta (y tolerante a Doze)
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } catch (_: SecurityException) {
            // Fallback si el sistema bloquea exact alarms
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancel(context: Context, scheduleId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RoutineReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}
