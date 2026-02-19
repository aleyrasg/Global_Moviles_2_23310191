package com.example.global_moviles_2_23310191.ui.routines

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.data.model.Routine
import com.example.global_moviles_2_23310191.notifications.ReminderScheduler
import com.example.global_moviles_2_23310191.ui.reminders.ReminderRepo
import com.example.global_moviles_2_23310191.ui.reminders.RoutineReminder
import com.example.global_moviles_2_23310191.utils.nextWeeklyTriggerMillis
import java.util.Locale

@Composable
fun RoutineFormScreen(
    navController: NavController,
    routineId: String?,
    vm: RoutineViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val state by vm.state.collectAsState()

    val editingRoutine = remember(routineId) {
        // Try to find the routine in the current state, but only if the list is loaded
        if (state.routines.isNotEmpty()) {
            state.routines.firstOrNull { it.id == routineId }
        } else null
    }

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var durationHour by remember { mutableStateOf(0) }
    var durationMinute by remember { mutableStateOf(0) }
    var reminderHour by remember { mutableStateOf(8) }
    var reminderMinute by remember { mutableStateOf(0) }
    var reminderDays by remember { mutableStateOf(listOf<Int>()) }

    val reminderRepo = remember { ReminderRepo(ctx) }

    fun scheduleReminders(routineId: String, routineName: String) {
        // cancelar anteriores
        reminderRepo.getByRoutineId(routineId).forEach { old ->
            ReminderScheduler.cancel(ctx, old.scheduleId)
        }
        reminderRepo.deleteByRoutineId(routineId)

        if (reminderDays.isEmpty() || reminderHour < 0 || reminderMinute < 0) return

        reminderDays.forEach { day ->
            val scheduleId = (routineId.hashCode() * 31) + day
            val triggerAt = nextWeeklyTriggerMillis(day, reminderHour, reminderMinute)
            ReminderScheduler.scheduleWeeklyRoutine(
                context = ctx,
                scheduleId = scheduleId,
                triggerAtMillis = triggerAt,
                routineName = routineName,
                routineId = routineId,
                dayOfWeek = day,
                hour = reminderHour,
                minute = reminderMinute
            )
            reminderRepo.upsert(
                RoutineReminder(
                    scheduleId = scheduleId,
                    routineId = routineId,
                    routineName = routineName,
                    dayOfWeek = day,
                    hour = reminderHour,
                    minute = reminderMinute
                )
            )
        }
    }

    // This effect will run when editingRoutine is found or changes.
    LaunchedEffect(editingRoutine) {
        if (editingRoutine != null) {
            name = editingRoutine.name
            type = editingRoutine.type
            durationHour = editingRoutine.durationMin / 60
            durationMinute = editingRoutine.durationMin % 60
            duration = String.format(Locale.US, "%02d:%02d", durationHour, durationMinute)
            notes = editingRoutine.notes
            reminderHour = editingRoutine.reminderHour
            reminderMinute = editingRoutine.reminderMinute
            reminderDays = editingRoutine.reminderDays
        }
    }

    // Load routines if they are not already loaded
    LaunchedEffect(Unit) {
        if (state.routines.isEmpty()) {
            vm.load()
        }
    }

    // Navigate back when the routine is saved
    LaunchedEffect(state.saved) {
        if (state.saved) {
            val message = if (routineId == null) "Rutina creada ✅" else "Rutina actualizada ✅"
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            vm.resetSavedState() // Reset the saved state
        }
    }

    // Show error messages
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(ctx, it, Toast.LENGTH_LONG).show()
        }
    }

    RoutineFormContent(
        title = if (routineId == null) "Nueva Rutina" else "Editar Rutina",
        name = name,
        type = type,
        duration = duration,
        notes = notes,
        loading = state.loading,
        durationHour = durationHour,
        durationMinute = durationMinute,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute,
        reminderDays = reminderDays,
        onNameChange = { name = it },
        onTypeChange = { type = it },
        onDurationChange = { },
        onNotesChange = { notes = it },
        onPickDuration = {
            TimePickerDialog(ctx, { _, h, m ->
                durationHour = h
                durationMinute = m
                duration = String.format(Locale.US, "%02d:%02d", h, m)
            }, durationHour, durationMinute, true).show()
        },
        onPickReminderTime = {
            TimePickerDialog(ctx, { _, h, m ->
                reminderHour = h
                reminderMinute = m
            }, reminderHour, reminderMinute, true).show()
        },
        onToggleReminderDay = { day ->
            reminderDays = if (reminderDays.contains(day)) {
                reminderDays.filterNot { it == day }
            } else {
                reminderDays + day
            }
        },
        onSaveClick = {
            val n = name.trim()
            val t = type.trim()
            val d = (durationHour * 60) + durationMinute

            if (n.isEmpty() || t.isEmpty() || d <= 0) {
                Toast.makeText(ctx, "Completa nombre, tipo y duracion > 0", Toast.LENGTH_SHORT).show()
                return@RoutineFormContent
            }

            if (routineId == null) {
                vm.create(
                    Routine(
                        name = n,
                        type = t,
                        durationMin = d,
                        notes = notes.trim(),
                        reminderDays = reminderDays,
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute
                    )
                ) { newId ->
                    scheduleReminders(newId, n)
                }
            } else {
                val routineToUpdate = editingRoutine?.copy(
                    name = n,
                    type = t,
                    durationMin = d,
                    notes = notes.trim(),
                    reminderDays = reminderDays,
                    reminderHour = reminderHour,
                    reminderMinute = reminderMinute
                )
                if (routineToUpdate != null) {
                    vm.update(routineToUpdate)
                    scheduleReminders(routineToUpdate.id, routineToUpdate.name)
                }
            }
        },
        onCancelClick = { navController.popBackStack() },
        isEditing = routineId != null
    )
}
