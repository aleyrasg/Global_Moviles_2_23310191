package com.example.global_moviles_2_23310191.ui.routines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.global_moviles_2_23310191.data.model.Routine

@Composable
fun RoutineListContent(
    routines: List<Routine>,
    query: String,
    onQueryChange: (String) -> Unit,
    loading: Boolean,
    error: String?,
    onAddClick: () -> Unit,
    onRoutineClick: (Routine) -> Unit,
    onDeleteRoutine: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
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
                onValueChange = onQueryChange,
                label = { Text("Buscar por nombre o tipo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            error?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            if (loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(routines) { routine ->
                    RoutineCard(
                        routine = routine,
                        onClick = { onRoutineClick(routine) },
                        onDelete = { onDeleteRoutine(routine.id) }
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
            .clickable(onClick = onClick)
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
