package com.example.global_moviles_2_23310191.ui.routines

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.global_moviles_2_23310191.utils.formatReminderTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineFormContent(
    title: String,
    name: String,
    type: String,
    duration: String,
    notes: String,
    loading: Boolean,
    durationHour: Int,
    durationMinute: Int,
    reminderHour: Int,
    reminderMinute: Int,
    reminderDays: List<Int>,
    onNameChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onPickDuration: () -> Unit,
    onPickReminderTime: () -> Unit,
    onToggleReminderDay: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    isEditing: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = type,
            onValueChange = onTypeChange,
            label = { Text("Tipo (push/pull/pierna/cardio)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = duration,
            onValueChange = onDurationChange,
            label = { Text("Duracion (hh:mm)") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = onPickDuration) { Text("Cambiar") }
            }
        )

        Spacer(Modifier.height(10.dp))

        Text("Recordatorio", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = formatReminderTime(reminderHour, reminderMinute),
            onValueChange = {},
            label = { Text("Hora") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = onPickReminderTime) { Text("Cambiar") }
            }
        )

        Spacer(Modifier.height(8.dp))

        Text("Dias", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            DayChip(label = "Dom", day = 1, selectedDays = reminderDays, onToggle = onToggleReminderDay)
            DayChip(label = "Lun", day = 2, selectedDays = reminderDays, onToggle = onToggleReminderDay)
            DayChip(label = "Mar", day = 3, selectedDays = reminderDays, onToggle = onToggleReminderDay)
            DayChip(label = "Mie", day = 4, selectedDays = reminderDays, onToggle = onToggleReminderDay)
        }
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            DayChip(label = "Jue", day = 5, selectedDays = reminderDays, onToggle = onToggleReminderDay)
            DayChip(label = "Vie", day = 6, selectedDays = reminderDays, onToggle = onToggleReminderDay)
            DayChip(label = "Sab", day = 7, selectedDays = reminderDays, onToggle = onToggleReminderDay)
        }

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(Modifier.height(16.dp))

        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text(if (isEditing) "Actualizar" else "Guardar")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = onCancelClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
        }
    }
}

@Composable
private fun DayChip(
    label: String,
    day: Int,
    selectedDays: List<Int>,
    onToggle: (Int) -> Unit
) {
    FilterChip(
        selected = selectedDays.contains(day),
        onClick = { onToggle(day) },
        label = { Text(label) }
    )
}
