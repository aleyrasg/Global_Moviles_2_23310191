package com.example.global_moviles_2_23310191.ui.progress

import android.widget.Toast
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
import com.example.global_moviles_2_23310191.data.model.ProgressEntry
import com.example.global_moviles_2_23310191.ui.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressListScreen(
    navController: NavController,
    vm: ProgressViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val state by vm.state.collectAsState()

    // ✅ Cargar al entrar
    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Progreso") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.PROGRESS_FORM) }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar entrada")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            state.error?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            if (state.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
            }

            if (!state.loading && state.entries.isEmpty()) {
                Text(
                    "No hay entradas de progreso. Presiona + para agregar una.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.entries) { entry ->
                        ProgressCard(
                            entry = entry,
                            onDelete = {
                                vm.delete(entry.id)
                                Toast.makeText(ctx, "Entrada eliminada", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(
    entry: ProgressEntry,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateStr = dateFormat.format(Date(entry.dateMillis))
    val timeStr = String.format(Locale.getDefault(), "%02d:%02d", entry.hour, entry.minute)

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("$dateStr $timeStr", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text("Cintura: ${entry.waistCm} cm", style = MaterialTheme.typography.bodyMedium)
                Text("Bícep: ${entry.bicepCm} cm", style = MaterialTheme.typography.bodyMedium)
                Text("Glúteo: ${entry.gluteCm} cm", style = MaterialTheme.typography.bodyMedium)

                if (entry.photoUrls.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text("📷 ${entry.photoUrls.size} foto(s)", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}
