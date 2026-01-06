package com.example.ai_fitness_workout_manager.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class WeightEntry(
    val id: String = "",
    val weightKg: Float = 0f,
    val date: String = "", // Format: yyyy-MM-dd
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "" // Optional note (e.g., "After workout", "Morning weigh-in")
) {
    constructor() : this(id = "")

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "weightKg" to weightKg,
            "date" to date,
            "timestamp" to timestamp,
            "note" to note
        )
    }
}
