package com.example.ai_fitness_workout_manager.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserProfile(
    var fullName: String = "",
    var dateOfBirth: String = "", // Format: YYYY-MM-DD
    var gender: String = "", // male, female, other, prefer_not_to_say
    var heightCm: Int = 0,
    var currentWeightKg: Float = 0f,
    var targetWeightKg: Float = 0f,
    var fitnessGoals: List<String> = emptyList(), // lose_fat, build_muscle, maintain_weight, improve_endurance, increase_flexibility, general_health
    var activityLevel: String = "", // sedentary, lightly_active, moderately_active, very_active, extra_active
    var workoutExperience: String = "", // beginner, intermediate, advanced
    var preferredWorkouts: List<String> = emptyList(), // strength, cardio, hiit, yoga, swimming, running, cycling, sports, home, gym
    var availableEquipment: List<String> = emptyList(), // none, dumbbells, barbell, resistance_bands, pullup_bar, kettlebells, full_gym, cardio_machines
    var workoutDaysPerWeek: Int = 3,
    var workoutDurationMinutes: Int = 45,
    var dietaryPreferences: List<String> = emptyList(), // no_restrictions, vegetarian, vegan, pescatarian, keto, gluten_free, dairy_free, halal, kosher
    var healthConditions: List<String> = emptyList(), // none, back_problems, knee_issues, shoulder_problems, heart_condition, diabetes, high_blood_pressure, other
    var sleepHoursPerNight: Int = 7,
    var profileCompleted: Boolean = false,
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor required for Firebase
    constructor() : this(fullName = "")

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "fullName" to fullName,
            "dateOfBirth" to dateOfBirth,
            "gender" to gender,
            "heightCm" to heightCm,
            "currentWeightKg" to currentWeightKg,
            "targetWeightKg" to targetWeightKg,
            "fitnessGoals" to fitnessGoals,
            "activityLevel" to activityLevel,
            "workoutExperience" to workoutExperience,
            "preferredWorkouts" to preferredWorkouts,
            "availableEquipment" to availableEquipment,
            "workoutDaysPerWeek" to workoutDaysPerWeek,
            "workoutDurationMinutes" to workoutDurationMinutes,
            "dietaryPreferences" to dietaryPreferences,
            "healthConditions" to healthConditions,
            "sleepHoursPerNight" to sleepHoursPerNight,
            "profileCompleted" to profileCompleted,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}
