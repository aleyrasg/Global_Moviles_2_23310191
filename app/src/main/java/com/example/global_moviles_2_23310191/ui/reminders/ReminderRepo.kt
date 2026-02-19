package com.example.global_moviles_2_23310191.ui.reminders

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class ReminderRepo(context: Context) {

    private val prefs = context.getSharedPreferences("routine_reminders", Context.MODE_PRIVATE)

    fun getAll(): List<RoutineReminder> {
        val raw = prefs.getString("items", "[]") ?: "[]"
        val arr = JSONArray(raw)
        val out = mutableListOf<RoutineReminder>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out += RoutineReminder(
                scheduleId = o.getInt("scheduleId"),
                routineId = o.getString("routineId"),
                routineName = o.getString("routineName"),
                dayOfWeek = o.getInt("dayOfWeek"),
                hour = o.getInt("hour"),
                minute = o.getInt("minute")
            )
        }
        return out
    }

    fun upsert(item: RoutineReminder) {
        val all = getAll().toMutableList()
        val idx = all.indexOfFirst { it.scheduleId == item.scheduleId }
        if (idx >= 0) all[idx] = item else all.add(item)
        saveAll(all)
    }

    fun delete(scheduleId: Int) {
        val all = getAll().filterNot { it.scheduleId == scheduleId }
        saveAll(all)
    }

    fun getByRoutineId(routineId: String): List<RoutineReminder> {
        return getAll().filter { it.routineId == routineId }
    }

    fun deleteByRoutineId(routineId: String) {
        val all = getAll().filterNot { it.routineId == routineId }
        saveAll(all)
    }

    private fun saveAll(list: List<RoutineReminder>) {
        val arr = JSONArray()
        list.forEach { r ->
            arr.put(
                JSONObject().apply {
                    put("scheduleId", r.scheduleId)
                    put("routineId", r.routineId)
                    put("routineName", r.routineName)
                    put("dayOfWeek", r.dayOfWeek)
                    put("hour", r.hour)
                    put("minute", r.minute)
                }
            )
        }
        prefs.edit().putString("items", arr.toString()).apply()
    }
}
