package com.example.global_moviles_2_23310191.data.model

data class Place(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val photoUrl: String? = null, // ✅ necesario para Módulo 7
    val createdAt: Long = System.currentTimeMillis()
)
