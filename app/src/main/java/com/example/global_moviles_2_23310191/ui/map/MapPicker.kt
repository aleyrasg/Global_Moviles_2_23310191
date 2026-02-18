package com.example.global_moviles_2_23310191.ui.map

import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    initial: LatLng = LatLng(20.6736, -103.344), // GDL default
    onCancel: () -> Unit,
    onConfirm: (picked: LatLng, resolvedAddress: String?) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var picked by remember { mutableStateOf(initial) }
    var resolvedAddress by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(picked) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(picked, 16f),
            durationMs = 400
        )
    }

    fun reverseGeocode(latLng: LatLng) {
        scope.launch {
            loading = true
            error = null
            resolvedAddress = withContext(Dispatchers.IO) {
                runCatching {
                    val geo = Geocoder(ctx, Locale.getDefault())
                    val list = geo.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    list?.firstOrNull()?.getAddressLine(0)
                }.getOrNull()
            }
            loading = false
        }
    }

    fun searchAddress(text: String) {
        scope.launch {
            loading = true
            error = null
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val geo = Geocoder(ctx, Locale.getDefault())
                    val list = geo.getFromLocationName(text, 1)
                    list?.firstOrNull()?.let { LatLng(it.latitude, it.longitude) }
                }.getOrNull()
            }
            loading = false
            if (result == null) {
                error = "No se encontró la dirección"
            } else {
                picked = result
                reverseGeocode(result)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Seleccionar ubicación") })
        },
        bottomBar = {
            Column(Modifier.padding(12.dp)) {
                if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
                resolvedAddress?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
                error?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancelar") }

                    Button(
                        onClick = { onConfirm(picked, resolvedAddress) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Usar ubicación") }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 🔎 Buscador
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                label = { Text("Buscar dirección") },
                trailingIcon = {
                    IconButton(onClick = { if (query.isNotBlank()) searchAddress(query.trim()) }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                },
                singleLine = true
            )

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true),
            ) {
                Marker(
                    state = MarkerState(position = picked),
                    title = "Lugar seleccionado"
                )
            }

            // 👆 Capturar tap en mapa
            LaunchedEffect(Unit) {
                // Nada aquí; el tap se maneja con MapEvents abajo
            }

            MapEffect(picked) { map ->
                map.setOnMapClickListener { latLng ->
                    picked = latLng
                    reverseGeocode(latLng)
                }
            }
        }
    }
}
