package com.example.global_moviles_2_23310191.data.model

data class Place(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val photoUrl: String? = null,

    // ✅ NUEVO para mapa
    val lat: Double? = null,
    val lng: Double? = null,

    // ✅ Para que PlaceRepository no truene
    val userId: String? = null,
    val createdAt: Long? = null
)
