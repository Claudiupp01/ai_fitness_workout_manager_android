package com.example.ai_fitness_workout_manager.ai

import com.example.ai_fitness_workout_manager.BuildConfig
import com.example.ai_fitness_workout_manager.model.UserProfile
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiWorkoutManager {

    private var workoutModel: GenerativeModel? = null

    private val workoutConfig = GenerationConfig.Builder().apply {
        temperature = 0.7f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 8192  // For detailed workout plans
    }.build()

    fun initialize(apiKey: String = BuildConfig.GEMINI_API_KEY) {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isEmpty()) {
            return
        }
        workoutModel = GenerativeModel(
            modelName = "gemini-2.5-flash-lite",
            apiKey = trimmedKey,
            generationConfig = workoutConfig
        )
    }

    fun hasValidApiKey(): Boolean = BuildConfig.GEMINI_API_KEY.trim().isNotEmpty()

    fun isInitialized(): Boolean = workoutModel != null

    /**
     * Generate a personalized weekly workout plan based on user profile
     */
    suspend fun generateWeeklyWorkoutPlan(userProfile: UserProfile): Result<String> = withContext(Dispatchers.IO) {
        try {
            val trimmedKey = BuildConfig.GEMINI_API_KEY.trim()
            if (trimmedKey.isEmpty()) {
                return@withContext Result.failure(Exception("API key not configured"))
            }

            val model = workoutModel ?: GenerativeModel(
                modelName = "gemini-2.5-flash-lite",
                apiKey = trimmedKey,
                generationConfig = workoutConfig
            )

            val prompt = buildWorkoutPrompt(userProfile)

            val inputContent = content {
                text(prompt)
            }

            val response = model.generateContent(inputContent)
            val responseText = response.text ?: "Could not generate workout plan. Please try again."

            Result.success(responseText)
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMessage = when {
                e.message?.contains("quota", ignoreCase = true) == true ->
                    "API quota exceeded. Please wait a few minutes or check your Gemini API quota at https://aistudio.google.com/"
                e.message?.contains("rate limit", ignoreCase = true) == true ->
                    "Rate limit exceeded. Please wait a moment before trying again."
                e.message?.contains("API key", ignoreCase = true) == true ->
                    "Invalid API key. Please check your Gemini API key configuration."
                e.message?.contains("UnexpectedResponse", ignoreCase = true) == true ->
                    "Unexpected API response. Please try again or check your API quota."
                e.message?.contains("blocked", ignoreCase = true) == true ->
                    "Content blocked by safety filters. Please try adjusting your profile settings."
                else -> "Error generating workout plan: ${e.message ?: "Unknown error"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    private fun buildWorkoutPrompt(profile: UserProfile): String {
        val goals = profile.fitnessGoals.joinToString(", ") { formatGoal(it) }
        val equipment = profile.availableEquipment.joinToString(", ") { formatEquipment(it) }
        val preferredWorkouts = profile.preferredWorkouts.joinToString(", ") { formatWorkout(it) }
        val healthConditions = profile.healthConditions.joinToString(", ") { formatHealthCondition(it) }

        return """
            Create a personalized weekly workout plan for a user with the following profile:

            USER PROFILE:
            - Fitness Goals: ${goals.ifEmpty { "General fitness" }}
            - Workout Experience: ${formatExperience(profile.workoutExperience)}
            - Available Equipment: ${equipment.ifEmpty { "No equipment (bodyweight only)" }}
            - Preferred Workout Types: ${preferredWorkouts.ifEmpty { "Any type" }}
            - Workout Days Per Week: ${profile.workoutDaysPerWeek}
            - Workout Duration: ${profile.workoutDurationMinutes} minutes per session
            - Health Considerations: ${healthConditions.ifEmpty { "None" }}

            Create a ${profile.workoutDaysPerWeek}-day workout plan (the remaining days will be rest days).

            For EACH DAY, provide the following in this EXACT format:

            DAY: [Day name - Monday, Tuesday, etc.]
            TYPE: [Workout type - Strength, Cardio, HIIT, Yoga, or Rest]
            DURATION: [Number only - duration in minutes]
            DIFFICULTY: [Easy, Medium, or Hard]
            TARGET: [Muscle groups - e.g., "Chest, Triceps, Shoulders"]

            EXERCISES:
            1. [Exercise Name]
               - Sets: [number]
               - Reps: [number or time]
               - Rest: [seconds]
               - Notes: [Brief form tips or modifications]

            2. [Exercise Name]
               - Sets: [number]
               - Reps: [number or time]
               - Rest: [seconds]
               - Notes: [Brief form tips or modifications]

            [Continue for all exercises in that day]

            ---

            IMPORTANT FORMATTING RULES:
            1. Always use the exact field names: DAY, TYPE, DURATION, DIFFICULTY, TARGET, EXERCISES
            2. Use "---" to separate each day
            3. For rest days, use TYPE: Rest and skip the exercises section
            4. Keep exercise count to 5-8 exercises per workout day
            5. Adjust difficulty based on experience level
            6. Consider health conditions when selecting exercises
            7. Match workout types to user preferences when possible
            8. Ensure equipment matches what's available
            9. Balance muscle groups throughout the week
            10. Include warm-up and cool-down in the exercise count

            Generate all 7 days (${profile.workoutDaysPerWeek} workout days + ${7 - profile.workoutDaysPerWeek} rest days).
        """.trimIndent()
    }

    // Helper formatting functions
    private fun formatGoal(goal: String): String = when (goal) {
        "lose_fat" -> "Lose Fat"
        "build_muscle" -> "Build Muscle"
        "maintain_weight" -> "Maintain Weight"
        "improve_endurance" -> "Improve Endurance"
        "increase_flexibility" -> "Increase Flexibility"
        "general_health" -> "General Health"
        else -> goal
    }

    private fun formatExperience(exp: String): String = when (exp) {
        "beginner" -> "Beginner"
        "intermediate" -> "Intermediate"
        "advanced" -> "Advanced"
        else -> "Beginner"
    }

    private fun formatWorkout(workout: String): String = when (workout) {
        "strength" -> "Strength Training"
        "cardio" -> "Cardio"
        "hiit" -> "HIIT"
        "yoga" -> "Yoga"
        "swimming" -> "Swimming"
        "running" -> "Running"
        "cycling" -> "Cycling"
        "sports" -> "Sports"
        "home" -> "Home Workouts"
        "gym" -> "Gym Workouts"
        else -> workout
    }

    private fun formatEquipment(equipment: String): String = when (equipment) {
        "none" -> "No Equipment"
        "dumbbells" -> "Dumbbells"
        "barbell" -> "Barbell"
        "resistance_bands" -> "Resistance Bands"
        "pullup_bar" -> "Pull-up Bar"
        "kettlebells" -> "Kettlebells"
        "full_gym" -> "Full Gym Access"
        "cardio_machines" -> "Cardio Machines"
        else -> equipment
    }

    private fun formatHealthCondition(condition: String): String = when (condition) {
        "none" -> "None"
        "back_problems" -> "Back Problems"
        "knee_issues" -> "Knee Issues"
        "shoulder_problems" -> "Shoulder Problems"
        "heart_condition" -> "Heart Condition"
        "diabetes" -> "Diabetes"
        "high_blood_pressure" -> "High Blood Pressure"
        "other" -> "Other"
        else -> condition
    }
}
