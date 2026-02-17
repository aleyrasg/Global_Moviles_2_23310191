package com.example.global_moviles_2_23310191.ui.routines

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.data.model.Routine

@Composable
fun RoutineFormScreen(
    navController: NavController,
    routineId: String?,
    vm: RoutineViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val state by vm.state.collectAsState()

    // Si es editar, buscamos rutina en memoria (ya cargada en List)
    val editingRoutine = remember(state.routines, routineId) {
        state.routines.firstOrNull { it.id == routineId }
    }

    var name by remember { mutableStateOf(editingRoutine?.name ?: "") }
    var type by remember { mutableStateOf(editingRoutine?.type ?: "") }
    var duration by remember { mutableStateOf(if (editingRoutine != null) editingRoutine.durationMin.toString() else "") }
    var notes by remember { mutableStateOf(editingRoutine?.notes ?: "") }

    LaunchedEffect(Unit) {
        // Si entras directo al form sin haber cargado lista, carga:
        if (state.routines.isEmpty()) vm.load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (routineId == null) "Nueva Rutina" else "Editar Rutina",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Tipo (push/pull/pierna/cardio)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it.filter { ch -> ch.isDigit() } },
            label = { Text("Duración (min)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(Modifier.height(16.dp))

        if (state.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = {
                val n = name.trim()
                val t = type.trim()
                val d = duration.toIntOrNull() ?: 0

                if (n.isEmpty() || t.isEmpty() || d <= 0) {
                    Toast.makeText(ctx, "Completa nombre, tipo y duración > 0", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (routineId == null) {
                    vm.create(
                        Routine(name = n, type = t, durationMin = d, notes = notes.trim())
                    ) {
                        Toast.makeText(ctx, "Rutina creada ✅", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                } else {
                    val base = editingRoutine ?: Routine(id = routineId)
                    vm.update(
                        base.copy(name = n, type = t, durationMin = d, notes = notes.trim())
                    ) {
                        Toast.makeText(ctx, "Rutina actualizada ✅", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading
        ) {
            Text(if (routineId == null) "Guardar" else "Actualizar")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}
