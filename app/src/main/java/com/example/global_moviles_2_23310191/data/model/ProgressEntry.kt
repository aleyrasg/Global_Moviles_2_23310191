package com.example.global_moviles_2_23310191.data.model

data class ProgressEntry(
    val id: String = "",
    val userId: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val hour: Int = 0,
    val minute: Int = 0,
    val waistCm: Double = 0.0,
    val bicepCm: Double = 0.0,
    val gluteCm: Double = 0.0,
    val photoUrls: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

