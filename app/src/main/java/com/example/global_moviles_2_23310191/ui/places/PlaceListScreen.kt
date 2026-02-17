package com.example.global_moviles_2_23310191.ui.places

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
import com.example.global_moviles_2_23310191.data.model.Place
import com.example.global_moviles_2_23310191.ui.navigation.Routes

@Composable
fun PlaceListScreen(navController: NavController, vm: PlaceViewModel = viewModel()) {
    val ctx = LocalContext.current
    val state by vm.state.collectAsState()

    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.load() }

    val filtered = remember(state.places, query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) state.places
        else state.places.filter {
            it.name.lowercase().contains(q) ||
                    it.address.lowercase().contains(q) ||
                    it.description.lowercase().contains(q)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Routes.PLACE_FORM) // crear
            }) { Icon(Icons.Default.Add, contentDescription = "Add") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Lugares", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar por nombre/dirección/descripción") },
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
                items(filtered) { place ->
                    PlaceCard(
                        place = place,
                        onClick = {
                            navController.navigate("${Routes.PLACE_FORM}/${place.id}")
                        },
                        onDelete = {
                            vm.delete(place.id)
                            Toast.makeText(ctx, "Lugar eliminado", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceCard(
    place: Place,
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
                Text(place.name, style = MaterialTheme.typography.titleMedium)
                if (place.address.isNotBlank()) {
                    Text(place.address, style = MaterialTheme.typography.bodyMedium)
                }
                if (place.description.isNotBlank()) {
                    Text(place.description, style = MaterialTheme.typography.bodySmall)
                }
                if (!place.photoUrl.isNullOrBlank()) {
                    Text("📷 Foto guardada", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
