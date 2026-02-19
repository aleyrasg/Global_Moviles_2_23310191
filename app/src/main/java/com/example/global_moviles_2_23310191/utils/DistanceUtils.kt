package com.example.global_moviles_2_23310191.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * Calcula la distancia en km entre dos puntos usando Haversine formula
 * @param from Coordenadas de origen
 * @param to Coordenadas de destino
 * @return Distancia en km
 */
fun calculateDistanceInKm(from: LatLng?, to: LatLng?): Double {
    if (from == null || to == null) return 0.0

    val results = FloatArray(1)
    Location.distanceBetween(
        from.latitude, from.longitude,
        to.latitude, to.longitude,
        results
    )

    return (results[0] / 1000).toDouble() // Convertir a km
}

/**
 * Formatea distancia para mostrar en UI
 * @param distanceKm Distancia en km
 * @return String formateado (ej: "2.3 km")
 */
fun formatDistance(distanceKm: Double): String {
    return when {
        distanceKm < 0.1 -> "<0.1 km"
        distanceKm < 1 -> String.format(Locale.US, "%.2f km", distanceKm)
        else -> String.format(Locale.US, "%.1f km", distanceKm)
    }
}

