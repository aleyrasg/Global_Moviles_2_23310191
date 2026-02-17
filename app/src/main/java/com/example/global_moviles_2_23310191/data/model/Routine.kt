package com.example.global_moviles_2_23310191.data.model

data class Routine(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "",          // push/pull/pierna/cardio
    val durationMin: Int = 0,
    val notes: String = "",
    val audioUrl: String? = null,   // lo usamos en módulo multimedia
    val createdAt: Long = System.currentTimeMillis()
)
