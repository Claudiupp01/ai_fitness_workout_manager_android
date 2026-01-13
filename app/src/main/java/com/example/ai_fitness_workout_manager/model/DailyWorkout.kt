package com.example.ai_fitness_workout_manager.model

data class DailyWorkout(
    val dayOfWeek: String,           // "Monday", "Tuesday", etc.
    val workoutType: String,          // "Strength", "Cardio", "HIIT", "Yoga", "Rest"
    val duration: Int,                // Duration in minutes
    val difficulty: String,           // "Easy", "Medium", "Hard"
    val exerciseCount: Int,           // Number of exercises
    val targetMuscles: List<String>,  // ["Chest", "Triceps", "Shoulders"]
    val exercises: List<Exercise>,    // List of exercises for this day
    val isRestDay: Boolean = false    // Whether this is a rest/recovery day
)

data class Exercise(
    val name: String,                 // "Barbell Bench Press"
    val sets: Int,                    // Number of sets
    val reps: String,                 // "8-12" or "30 seconds" for timed exercises
    val restTime: Int,                // Rest time in seconds between sets
    val notes: String = "",           // Additional instructions or tips
    val muscleGroup: String           // "Chest", "Back", "Legs", etc.
)

data class WeeklyWorkoutPlan(
    val weekTitle: String,            // "Week of Jan 13 - Jan 19"
    val generatedDate: String,        // Date when plan was generated
    val fitnessGoal: String,          // User's primary goal
    val workouts: List<DailyWorkout>  // 7 days of workouts
)
