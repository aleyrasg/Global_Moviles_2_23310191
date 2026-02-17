package com.example.global_moviles_2_23310191.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.tasks.await

@Composable
fun MapScreen() {
    val ctx = LocalContext.current

    var hasLocationPermission by remember { mutableStateOf(false) }
    var myLocation by remember { mutableStateOf<LatLng?>(null) }

    var destination by remember { mutableStateOf(LatLng(20.6736, -103.344)) } // GDL

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
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            myLocation = getLastLocationLatLng(ctx)
        }
    }

    // ✅ Primero declara cameraPositionState
    val cameraPositionState = rememberCameraPositionState()

    // ✅ Luego úsalo
    LaunchedEffect(myLocation, destination) {
        val target = myLocation ?: destination
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(target, 14f),
            durationMs = 700
        )
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { destination = LatLng(20.6597, -103.3496) },
                modifier = Modifier.weight(1f)
            ) { Text("Destino A") }

            OutlinedButton(
                onClick = { destination = LatLng(20.6767, -103.3475) },
                modifier = Modifier.weight(1f)
            ) { Text("Destino B") }
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = true
            )
        ) {
            Marker(
                state = MarkerState(position = destination),
                title = "Destino"
            )

            myLocation?.let { me ->
                Marker(
                    state = MarkerState(position = me),
                    title = "Yo"
                )
                Polyline(points = listOf(me, destination))
            }
        }
    }
}

@SuppressLint("MissingPermission")
private suspend fun getLastLocationLatLng(context: Context): LatLng? {
    val fused = LocationServices.getFusedLocationProviderClient(context)
    val loc: Location? = runCatching { fused.lastLocation.await() }.getOrNull()
    return loc?.let { LatLng(it.latitude, it.longitude) }
}
