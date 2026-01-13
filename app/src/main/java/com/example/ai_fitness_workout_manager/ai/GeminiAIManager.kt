package com.example.ai_fitness_workout_manager.ai

import com.example.ai_fitness_workout_manager.BuildConfig
import com.example.ai_fitness_workout_manager.model.UserProfile
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiAIManager {

    private var generativeModel: GenerativeModel? = null
    private var chatHistory: MutableList<Content> = mutableListOf()
    private var userProfile: UserProfile? = null

    private val generationConfig = GenerationConfig.Builder().apply {
        temperature = 0.7f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 1500
    }.build()

    fun initialize(apiKey: String = BuildConfig.GEMINI_API_KEY) {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isEmpty()) {
            return // Don't initialize without a valid API key
        }
        generativeModel = GenerativeModel(
            modelName = "gemini-2.5-flash-lite",
            apiKey = trimmedKey,
            generationConfig = generationConfig
        )
        chatHistory.clear()
    }

    fun hasValidApiKey(): Boolean = BuildConfig.GEMINI_API_KEY.trim().isNotEmpty()

    fun setUserProfile(profile: UserProfile?) {
        userProfile = profile
    }

    private fun buildSystemPrompt(): String {
        val profileContext = userProfile?.let { profile ->
            """

            USER PROFILE INFORMATION:
            - Name: ${profile.fullName.ifEmpty { "Not provided" }}
            - Age: ${calculateAge(profile.dateOfBirth)} years old
            - Gender: ${formatGender(profile.gender)}
            - Height: ${profile.heightCm} cm
            - Current Weight: ${profile.currentWeightKg} kg
            - Target Weight: ${profile.targetWeightKg} kg
            - Fitness Goals: ${profile.fitnessGoals.joinToString(", ") { formatGoal(it) }.ifEmpty { "Not specified" }}
            - Activity Level: ${formatActivityLevel(profile.activityLevel)}
            - Workout Experience: ${formatExperience(profile.workoutExperience)}
            - Preferred Workouts: ${profile.preferredWorkouts.joinToString(", ") { formatWorkout(it) }.ifEmpty { "Not specified" }}
            - Available Equipment: ${profile.availableEquipment.joinToString(", ") { formatEquipment(it) }.ifEmpty { "Not specified" }}
            - Workout Days Per Week: ${profile.workoutDaysPerWeek}
            - Workout Duration: ${profile.workoutDurationMinutes} minutes
            - Dietary Preferences: ${profile.dietaryPreferences.joinToString(", ") { formatDietary(it) }.ifEmpty { "No restrictions" }}
            - Health Conditions: ${profile.healthConditions.joinToString(", ") { formatHealthCondition(it) }.ifEmpty { "None" }}
            - Sleep Hours: ${profile.sleepHoursPerNight} hours per night

            Use this information to provide personalized advice and recommendations.
            """.trimIndent()
        } ?: ""

        return """
You are FitCoach AI, a friendly, supportive, and knowledgeable AI assistant specialized in fitness, nutrition, and wellness. You are part of the AI Fitness Workout Manager app.

YOUR ROLE AND PERSONALITY:
- You are warm, encouraging, and motivating - like a supportive personal trainer and nutritionist combined
- You celebrate user achievements and progress, no matter how small
- You provide evidence-based advice while being practical and realistic
- You understand that fitness journeys have ups and downs and approach setbacks with empathy
- You use a conversational, friendly tone while maintaining professionalism
- You occasionally use relevant emojis to make conversations more engaging (but don't overdo it)

YOUR EXPERTISE AREAS:
1. NUTRITION & MEAL PLANNING:
   - Creating balanced meal plans based on user goals (weight loss, muscle gain, maintenance)
   - Providing healthy recipe suggestions and meal ideas
   - Explaining macronutrients (protein, carbs, fats) and their roles
   - Offering advice on portion control and calorie management
   - Suggesting healthy alternatives and substitutions
   - Addressing dietary restrictions and preferences

2. WORKOUT & EXERCISE:
   - Designing workout routines appropriate for the user's fitness level
   - Explaining proper exercise form and techniques
   - Suggesting exercises for specific muscle groups or goals
   - Creating home workouts or gym-based routines based on available equipment
   - Recommending warm-up and cool-down routines
   - Providing stretching and flexibility guidance

3. WELLNESS & LIFESTYLE:
   - Offering tips for better sleep and recovery
   - Discussing the importance of rest days
   - Providing motivation and mental wellness support
   - Helping set realistic and achievable goals
   - Tracking progress and celebrating milestones

GUIDELINES:
- Always prioritize safety - recommend consulting healthcare professionals for medical concerns
- Be specific and actionable in your advice
- Ask clarifying questions when needed to provide better recommendations
- Keep responses concise but informative (avoid walls of text)
- When suggesting workouts, consider the user's equipment and experience level
- When suggesting meals, consider dietary preferences and restrictions
- If you don't know something, admit it honestly rather than making things up
- Never provide medical diagnoses or replace professional medical advice
$profileContext

Remember: Your goal is to help users achieve their fitness goals while making the journey enjoyable and sustainable. Be their supportive companion on this journey!
        """.trimIndent()
    }

    suspend fun sendMessage(userMessage: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val model = generativeModel ?: return@withContext Result.failure(
                Exception("AI not initialized. Please set your API key.")
            )

            // Build the full prompt with system context for the first message or include it
            val fullPrompt = if (chatHistory.isEmpty()) {
                "${buildSystemPrompt()}\n\nUser: $userMessage"
            } else {
                userMessage
            }

            // Add user message to history
            chatHistory.add(content("user") { text(fullPrompt) })

            // Create chat with history
            val chat = model.startChat(chatHistory.dropLast(1))

            // Send message and get response
            val response = chat.sendMessage(fullPrompt)
            val responseText = response.text ?: "I apologize, but I couldn't generate a response. Please try again."

            // Add AI response to history
            chatHistory.add(content("model") { text(responseText) })

            Result.success(responseText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearChatHistory() {
        chatHistory.clear()
    }

    fun isInitialized(): Boolean = generativeModel != null

    // Helper formatting functions
    private fun calculateAge(dateOfBirth: String): Int {
        if (dateOfBirth.isEmpty()) return 0
        return try {
            val parts = dateOfBirth.split("-")
            if (parts.size != 3) return 0
            val birthYear = parts[0].toInt()
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            currentYear - birthYear
        } catch (e: Exception) {
            0
        }
    }

    private fun formatGender(gender: String): String = when (gender) {
        "male" -> "Male"
        "female" -> "Female"
        "other" -> "Other"
        else -> "Not specified"
    }

    private fun formatGoal(goal: String): String = when (goal) {
        "lose_fat" -> "Lose Fat"
        "build_muscle" -> "Build Muscle"
        "maintain_weight" -> "Maintain Weight"
        "improve_endurance" -> "Improve Endurance"
        "increase_flexibility" -> "Increase Flexibility"
        "general_health" -> "General Health"
        else -> goal
    }

    private fun formatActivityLevel(level: String): String = when (level) {
        "sedentary" -> "Sedentary"
        "lightly_active" -> "Lightly Active"
        "moderately_active" -> "Moderately Active"
        "very_active" -> "Very Active"
        "extra_active" -> "Extra Active"
        else -> "Not specified"
    }

    private fun formatExperience(exp: String): String = when (exp) {
        "beginner" -> "Beginner"
        "intermediate" -> "Intermediate"
        "advanced" -> "Advanced"
        else -> "Not specified"
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

    private fun formatDietary(pref: String): String = when (pref) {
        "no_restrictions" -> "No Restrictions"
        "vegetarian" -> "Vegetarian"
        "vegan" -> "Vegan"
        "pescatarian" -> "Pescatarian"
        "keto" -> "Keto"
        "gluten_free" -> "Gluten Free"
        "dairy_free" -> "Dairy Free"
        "halal" -> "Halal"
        "kosher" -> "Kosher"
        else -> pref
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
