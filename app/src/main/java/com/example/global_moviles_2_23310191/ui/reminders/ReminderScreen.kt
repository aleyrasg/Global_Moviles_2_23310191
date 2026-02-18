package com.example.global_moviles_2_23310191.ui.reminders

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.data.model.Routine
import com.example.global_moviles_2_23310191.notifications.ReminderScheduler
import com.example.global_moviles_2_23310191.ui.routines.RoutineViewModel
import com.example.global_moviles_2_23310191.utils.nextWeeklyTriggerMillis
import java.util.Calendar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    navController: NavController,
    routineVm: RoutineViewModel = viewModel()

) {
    val ctx = LocalContext.current
    val repo = remember { ReminderRepo(ctx) }

    val routineState by routineVm.state.collectAsState()
    var reminders by remember { mutableStateOf(repo.getAll()) }

    var schedulingRoutine by remember { mutableStateOf<Routine?>(null) }

    // Permiso Android 13+
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        routineVm.load()
        if (Build.VERSION.SDK_INT >= 33) {
            notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Ordenar recordatorios por día y hora
    val sortedReminders = remember(reminders) {
        reminders.sortedWith(
            compareBy<RoutineReminder> { it.dayOfWeek }
                .thenBy { it.hour }
                .thenBy { it.minute }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recordatorios de Rutinas") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text("Rutinas", style = MaterialTheme.typography.titleLarge)

            if (routineState.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (!routineState.loading && routineState.routines.isEmpty()) {
                Text(
                    "No hay rutinas creadas. Crea una rutina primero.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 260.dp)
                ) {
                    items(routineState.routines) { routine ->
                        RoutinePickCard(
                            routine = routine,
                            onSchedule = { schedulingRoutine = routine }
                        )
                    }
                }
            }

            Divider()

            Text("Agendadas (por día)", style = MaterialTheme.typography.titleLarge)

            if (sortedReminders.isEmpty()) {
                Text(
                    "Aún no tienes rutinas agendadas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sortedReminders) { item ->
                        ReminderItemCard(
                            item = item,
                            onDelete = {
                                // 1) Cancelar alarm
                                ReminderScheduler.cancel(ctx, item.scheduleId)
                                // 2) Borrar de storage
                                repo.delete(item.scheduleId)
                                // 3) Refrescar lista
                                reminders = repo.getAll()
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog de agendar
    schedulingRoutine?.let { routine ->
        ScheduleDialogNoExperimental(
            routineName = routine.name,
            onDismiss = { schedulingRoutine = null },
            onConfirm = { dayOfWeek, hour, minute ->
                val scheduleId = (routine.id + dayOfWeek + hour + minute).hashCode()

                val triggerAt = nextWeeklyTriggerMillis(dayOfWeek, hour, minute)

                // Guardar
                repo.upsert(
                    RoutineReminder(
                        scheduleId = scheduleId,
                        routineId = routine.id,
                        routineName = routine.name,
                        dayOfWeek = dayOfWeek,
                        hour = hour,
                        minute = minute
                    )
                )
                reminders = repo.getAll()

                // Programar (semanal por reprogramación en Receiver)
                ReminderScheduler.scheduleWeeklyRoutine(
                    context = ctx,
                    scheduleId = scheduleId,
                    triggerAtMillis = triggerAt,
                    routineName = routine.name,
                    routineId = routine.id,
                    dayOfWeek = dayOfWeek,
                    hour = hour,
                    minute = minute
                )

                schedulingRoutine = null
            }
        )
    }
}

@Composable
private fun RoutinePickCard(
    routine: Routine,
    onSchedule: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(routine.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Tipo: ${routine.type} • ${routine.durationMin} min",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onSchedule) { Text("Agendar") }
        }
    }
}

@Composable
private fun ReminderItemCard(
    item: RoutineReminder,
    onDelete: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.routineName, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${dayLabel(item.dayOfWeek)} • ${two(item.hour)}:${two(item.minute)}",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            OutlinedButton(onClick = onDelete) { Text("Borrar") }
        }
    }
}

@Composable
private fun ScheduleDialogNoExperimental(
    routineName: String,
    onDismiss: () -> Unit,
    onConfirm: (dayOfWeek: Int, hour: Int, minute: Int) -> Unit
) {
    var day by remember { mutableIntStateOf(Calendar.MONDAY) }
    var hour by remember { mutableIntStateOf(7) }
    var minute by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agendar rutina") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Rutina: $routineName")

                DayPicker(day = day, onChange = { day = it })

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = hour.toString(),
                        onValueChange = { v -> hour = v.filter { it.isDigit() }.toIntOrNull()?.coerceIn(0, 23) ?: 0 },
                        label = { Text("Hora") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minute.toString(),
                        onValueChange = { v -> minute = v.filter { it.isDigit() }.toIntOrNull()?.coerceIn(0, 59) ?: 0 },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    "Se repetirá cada semana en ese día hasta que lo borres.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = { Button(onClick = { onConfirm(day, hour, minute) }) { Text("Guardar") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun DayPicker(day: Int, onChange: (Int) -> Unit) {
    val options = listOf(
        Calendar.MONDAY to "Lunes",
        Calendar.TUESDAY to "Martes",
        Calendar.WEDNESDAY to "Miércoles",
        Calendar.THURSDAY to "Jueves",
        Calendar.FRIDAY to "Viernes",
        Calendar.SATURDAY to "Sábado",
        Calendar.SUNDAY to "Domingo"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChange(value) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (day == value), onClick = { onChange(value) })
                Spacer(Modifier.width(8.dp))
                Text(label)
            }
        }
    }
}

private fun two(n: Int) = n.toString().padStart(2, '0')

private fun dayLabel(day: Int): String = when (day) {
    Calendar.MONDAY -> "Lunes"
    Calendar.TUESDAY -> "Martes"
    Calendar.WEDNESDAY -> "Miércoles"
    Calendar.THURSDAY -> "Jueves"
    Calendar.FRIDAY -> "Viernes"
    Calendar.SATURDAY -> "Sábado"
    Calendar.SUNDAY -> "Domingo"
    else -> "Día"
}
