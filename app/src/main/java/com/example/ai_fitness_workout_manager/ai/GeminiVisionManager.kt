package com.example.ai_fitness_workout_manager.ai

import android.graphics.Bitmap
import com.example.ai_fitness_workout_manager.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiVisionManager {

    private var visionModel: GenerativeModel? = null

    private val visionConfig = GenerationConfig.Builder().apply {
        temperature = 0.4f  // Lower temperature for more consistent results
        topK = 32
        topP = 0.9f
        maxOutputTokens = 1024  // For image analysis
    }.build()

    private val recipeConfig = GenerationConfig.Builder().apply {
        temperature = 0.7f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 8192  // Increased for detailed recipe recommendations
    }.build()

    fun initialize(apiKey: String = BuildConfig.GEMINI_API_KEY) {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isEmpty()) {
            return
        }
        visionModel = GenerativeModel(
            modelName = "gemini-2.5-flash-lite",  // Ultra fast model optimized for cost-efficiency with vision support
            apiKey = trimmedKey,
            generationConfig = visionConfig  // Default to vision config
        )
    }

    fun hasValidApiKey(): Boolean = BuildConfig.GEMINI_API_KEY.trim().isNotEmpty()

    fun isInitialized(): Boolean = visionModel != null

    /**
     * Analyze a food/ingredient image and extract list of ingredients
     */
    suspend fun analyzeIngredientsImage(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            val model = visionModel ?: return@withContext Result.failure(
                Exception("Vision AI not initialized. Please set your API key.")
            )

            val prompt = """
                Analyze this image and identify all the cooking ingredients visible in it.

                INSTRUCTIONS:
                - List each ingredient on a new line starting with a dash (-)
                - Include approximate quantities if visible (e.g., "- 2 eggs", "- 200ml milk")
                - If quantity cannot be determined, just list the ingredient (e.g., "- flour")
                - Be specific about the ingredient type (e.g., "chicken breast" not just "chicken")
                - Only list food ingredients that can be used for cooking
                - Do not include cookware, utensils, or non-food items
                - Keep the list concise and practical

                FORMAT YOUR RESPONSE AS:
                - [quantity] ingredient name

                Example output:
                - 2 eggs
                - 200ml milk
                - 150g flour
                - 1 tsp salt
                - 50g butter

                If no ingredients are clearly visible in the image, respond with:
                "No clear cooking ingredients detected in this image. Please take another photo with visible ingredients."
            """.trimIndent()

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = model.generateContent(inputContent)
            val responseText = response.text ?: "Could not analyze the image. Please try again."

            Result.success(responseText)
        } catch (e: Exception) {
            e.printStackTrace()  // Log full error for debugging
            val errorMessage = when {
                e.message?.contains("quota", ignoreCase = true) == true ->
                    "API quota exceeded. Please wait a few minutes or check your Gemini API quota at https://aistudio.google.com/"
                e.message?.contains("rate limit", ignoreCase = true) == true ->
                    "Rate limit exceeded. Please wait a moment before trying again."
                e.message?.contains("API key", ignoreCase = true) == true ->
                    "Invalid API key. Please check your Gemini API key configuration."
                e.message?.contains("UnexpectedResponse", ignoreCase = true) == true ->
                    "Unexpected API response. The image might be too large or complex. Try a different image or manually enter ingredients."
                e.message?.contains("blocked", ignoreCase = true) == true ->
                    "Content blocked by safety filters. Please try a different image."
                else -> "Error analyzing image: ${e.message ?: "Unknown error"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Generate recipe recommendations based on ingredients
     */
    suspend fun getRecipeRecommendations(ingredients: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val trimmedKey = BuildConfig.GEMINI_API_KEY.trim()
            if (trimmedKey.isEmpty()) {
                return@withContext Result.failure(Exception("API key not configured"))
            }

            // Create a separate model instance for recipe generation with higher token limit
            val recipeModel = GenerativeModel(
                modelName = "gemini-2.5-flash-lite",
                apiKey = trimmedKey,
                generationConfig = recipeConfig
            )

            val prompt = """
                I have the following ingredients available:

                $ingredients

                Please recommend 3 different recipes I can make using these ingredients.

                For each recipe, provide:
                1. Recipe Name
                2. Brief Description (1 sentence)
                3. Difficulty Level (Easy/Medium/Hard)
                4. Prep Time + Cook Time
                5. Servings
                6. Ingredients needed (from my list)
                7. Step-by-step instructions (numbered, concise)
                8. Nutritional info (calories, protein, carbs, fat per serving)

                Keep each recipe concise but complete. Format clearly with headers.
            """.trimIndent()

            val inputContent = content {
                text(prompt)
            }

            val response = recipeModel.generateContent(inputContent)
            val responseText = response.text ?: "Could not generate recommendations. Please try again."

            Result.success(responseText)
        } catch (e: Exception) {
            e.printStackTrace()  // Log full error for debugging
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
                    "Content blocked by safety filters. Please modify your ingredients list."
                else -> "Error generating recommendations: ${e.message ?: "Unknown error"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }
}
