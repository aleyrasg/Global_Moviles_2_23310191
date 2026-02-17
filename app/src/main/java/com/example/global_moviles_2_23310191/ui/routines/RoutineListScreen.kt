package com.example.global_moviles_2_23310191.ui.routines

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.data.model.Routine
import com.example.global_moviles_2_23310191.ui.navigation.Routes

@Composable
fun RoutineListScreen(navController: NavController, vm: RoutineViewModel = viewModel()) {
    val ctx = LocalContext.current
    val state by vm.state.collectAsState()

    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.load() }

    val filtered = remember(state.routines, query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) state.routines
        else state.routines.filter {
            it.name.lowercase().contains(q) || it.type.lowercase().contains(q)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Routes.ROUTINE_FORM) // crear
            }) { Icon(Icons.Default.Add, contentDescription = "Add") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Rutinas", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar por nombre o tipo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            state.error?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            if (state.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { routine ->
                    RoutineCard(
                        routine = routine,
                        onClick = {
                            // editar: pasamos id por ruta
                            navController.navigate("${Routes.ROUTINE_FORM}/${routine.id}")
                        },
                        onDelete = {
                            vm.delete(routine.id)
                            Toast.makeText(ctx, "Rutina eliminada", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoutineCard(
    routine: Routine,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(routine.name, style = MaterialTheme.typography.titleMedium)
                Text("Tipo: ${routine.type} • ${routine.durationMin} min", style = MaterialTheme.typography.bodyMedium)
                if (routine.notes.isNotBlank()) {
                    Text(routine.notes, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
