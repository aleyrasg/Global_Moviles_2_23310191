package com.example.global_moviles_2_23310191.ui.places

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.data.model.Place
import com.example.global_moviles_2_23310191.ui.map.MapPickerScreen
import com.google.android.gms.maps.model.LatLng

@Composable
fun PlaceFormScreen(
    navController: NavController,
    placeId: String?,
    vm: PlaceViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val state by vm.state.collectAsState()

    // Si es editar, buscar en memoria (lista)
    val editingPlace = remember(state.places, placeId) {
        state.places.firstOrNull { it.id == placeId }
    }

    var name by remember(editingPlace) { mutableStateOf(editingPlace?.name ?: "") }
    var address by remember(editingPlace) { mutableStateOf(editingPlace?.address ?: "") }
    var description by remember(editingPlace) { mutableStateOf(editingPlace?.description ?: "") }

    // ✅ Estado para abrir/cerrar mapa y guardar coords
    var showMapPicker by remember { mutableStateOf(false) }
    var pickedLatLng by remember(editingPlace) {
        mutableStateOf(
            editingPlace?.lat?.let { lat ->
                editingPlace.lng?.let { lng -> LatLng(lat, lng) }
            }
        )
    }

    // uri local (nueva foto seleccionada)
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // picker galería
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedPhotoUri = uri
    }

    LaunchedEffect(Unit) {
        if (state.places.isEmpty()) vm.load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (placeId == null) "Nuevo Lugar" else "Editar Lugar",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading
        )

        Spacer(Modifier.height(10.dp))

        // ✅ Botón para elegir dirección desde mapa
        OutlinedButton(
            onClick = { showMapPicker = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading
        ) {
            Text(
                if (pickedLatLng == null) "Elegir ubicación en mapa"
                else "Cambiar ubicación en mapa"
            )
        }

        pickedLatLng?.let {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "📍 ${it.latitude}, ${it.longitude}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            enabled = !state.loading
        )

        Spacer(Modifier.height(14.dp))

        // ✅ Elegir foto (galería)
        OutlinedButton(
            onClick = { pickImage.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading
        ) {
            Text(
                if (selectedPhotoUri == null) "Elegir foto de galería"
                else "Cambiar foto seleccionada"
            )
        }

        Spacer(Modifier.height(8.dp))

        // ✅ Info de foto (depende de tu Place.photoUrl)
        val hasExistingPhoto = !editingPlace?.photoUrl.isNullOrBlank()
        when {
            selectedPhotoUri != null -> Text("✅ Foto seleccionada (se subirá al guardar)")
            hasExistingPhoto -> Text("📷 Ya tiene foto guardada (se mantiene si no cambias)")
            else -> Text("Sin foto")
        }

        Spacer(Modifier.height(16.dp))

        if (state.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = {
                val n = name.trim()
                val a = address.trim()
                val d = description.trim()

                if (n.isEmpty()) {
                    Toast.makeText(ctx, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val lat = pickedLatLng?.latitude
                val lng = pickedLatLng?.longitude

                if (placeId == null) {
                    vm.create(
                        place = Place(
                            name = n,
                            address = a,
                            description = d,
                            lat = lat,
                            lng = lng
                        ),
                        photoUri = selectedPhotoUri
                    ) {
                        Toast.makeText(ctx, "Lugar creado ✅", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                } else {
                    val base = editingPlace ?: Place(id = placeId)
                    vm.update(
                        place = base.copy(
                            name = n,
                            address = a,
                            description = d,
                            lat = lat,
                            lng = lng
                        ),
                        newPhotoUri = selectedPhotoUri
                    ) {
                        Toast.makeText(ctx, "Lugar actualizado ✅", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading
        ) {
            Text(if (placeId == null) "Guardar" else "Actualizar")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading
        ) {
            Text("Cancelar")
        }
    }

    // ✅ Overlay del mapa (picker)
    if (showMapPicker) {
        MapPickerScreen(
            initial = pickedLatLng ?: LatLng(20.6736, -103.344), // GDL default
            onCancel = { showMapPicker = false },
            onConfirm = { latLng, resolvedAddress ->
                pickedLatLng = latLng
                // opcional: autollenar dirección con lo que devuelve el geocoder
                if (!resolvedAddress.isNullOrBlank()) {
                    address = resolvedAddress
                }
                showMapPicker = false
            }
        )
    }
}
