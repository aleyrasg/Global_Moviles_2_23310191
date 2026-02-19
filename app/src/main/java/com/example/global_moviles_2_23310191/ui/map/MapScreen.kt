package com.example.global_moviles_2_23310191.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.global_moviles_2_23310191.ui.places.PlaceViewModel
import com.example.global_moviles_2_23310191.utils.calculateDistanceInKm
import com.example.global_moviles_2_23310191.utils.formatDistance
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.tasks.await

@Composable
fun MapScreen(
    navController: NavController,
    placeVm: PlaceViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val placeState by placeVm.state.collectAsState()

    var hasLocationPermission by remember { mutableStateOf(false) }
    var myLocation by remember { mutableStateOf<LatLng?>(null) }

    // ✅ FUNCIONALIDAD 3: Info window al tocar marcador
    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasLocationPermission =
            (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                    (perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        placeVm.load()
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            myLocation = getLastLocationLatLng(ctx)
        }
    }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(myLocation) {
        myLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 14f),
                durationMs = 700
            )
        }
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = true
            )
        ) {
            // ✅ FUNCIONALIDAD 1: Mostrar mi ubicación actual
            myLocation?.let { me ->
                Marker(
                    state = MarkerState(position = me),
                    title = "Mi ubicación"
                )
            }

            // ✅ FUNCIONALIDAD 1: Mostrar lugares registrados
            placeState.places.forEach { place ->
                if (place.lat != null && place.lng != null) {
                    Marker(
                        state = MarkerState(position = LatLng(place.lat, place.lng)),
                        title = place.name,
                        snippet = place.address,
                        onClick = {
                            selectedPlace = place
                            true
                        }
                    )
                }
            }
        }

        // ✅ FUNCIONALIDAD 2: Info window al tocar marcador
        selectedPlace?.let { place ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        place.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        place.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    myLocation?.let { me ->
                        val distanceKm =
                            calculateDistanceInKm(me, LatLng(place.lat!!, place.lng!!))
                        Text(
                            "Distancia: ${formatDistance(distanceKm)}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("${Routes.PLACE_FORM}/${place.id}")
                                selectedPlace = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Ver detalles")
                        }

                        OutlinedButton(
                            onClick = { selectedPlace = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cerrar")
                        }
                    }
                }
            }
        }

        // ✅ FUNCIONALIDAD 3: Botón crear lugar desde mapa
        FloatingActionButton(
            onClick = {
                if (myLocation != null) {
                    navController.navigate(
                        "${Routes.PLACE_FORM}?lat=${myLocation!!.latitude}&lng=${myLocation!!.longitude}"
                    )
                } else {
                    // Fallback a Guadalajara si no tenemos ubicación
                    navController.navigate(
                        "${Routes.PLACE_FORM}?lat=20.6736&lng=-103.344"
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Crear lugar")
        }
    }
}

@SuppressLint("MissingPermission")
private suspend fun getLastLocationLatLng(context: Context): LatLng? {
    val fused = LocationServices.getFusedLocationProviderClient(context)
    val loc: Location? = runCatching { fused.lastLocation.await() }.getOrNull()
    return loc?.let { LatLng(it.latitude, it.longitude) }
}
