package com.example.ai_fitness_workout_manager.utils

import android.content.Context
import com.example.ai_fitness_workout_manager.database.RecipeDatabase
import com.example.ai_fitness_workout_manager.model.MealRecipe
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DailyRecipeManager {

    private const val PREFS_NAME = "daily_recipes"
    private const val KEY_LAST_UPDATE_DATE = "last_update_date"
    private const val KEY_BREAKFAST_IDS = "breakfast_ids"
    private const val KEY_LUNCH_IDS = "lunch_ids"
    private const val KEY_DINNER_IDS = "dinner_ids"

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Get today's recipes. If recipes for today don't exist, generate new ones.
     */
    fun getTodaysRecipes(context: Context): DailyRecipes {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = dateFormat.format(Calendar.getInstance().time)
        val lastUpdateDate = prefs.getString(KEY_LAST_UPDATE_DATE, "")

        // Check if we need to generate new recipes (different day)
        if (lastUpdateDate != today) {
            return generateAndSaveNewRecipes(context, today)
        }

        // Load saved recipe IDs
        val breakfastIds = prefs.getString(KEY_BREAKFAST_IDS, "")?.split(",") ?: emptyList()
        val lunchIds = prefs.getString(KEY_LUNCH_IDS, "")?.split(",") ?: emptyList()
        val dinnerIds = prefs.getString(KEY_DINNER_IDS, "")?.split(",") ?: emptyList()

        // If any list is empty, regenerate
        if (breakfastIds.isEmpty() || lunchIds.isEmpty() || dinnerIds.isEmpty()) {
            return generateAndSaveNewRecipes(context, today)
        }

        // Retrieve recipes by IDs
        val breakfastRecipes = breakfastIds.mapNotNull { id ->
            RecipeDatabase.getRecipeById(id)
        }
        val lunchRecipes = lunchIds.mapNotNull { id ->
            RecipeDatabase.getRecipeById(id)
        }
        val dinnerRecipes = dinnerIds.mapNotNull { id ->
            RecipeDatabase.getRecipeById(id)
        }

        // If any list doesn't have 3 recipes, regenerate
        if (breakfastRecipes.size != 3 || lunchRecipes.size != 3 || dinnerRecipes.size != 3) {
            return generateAndSaveNewRecipes(context, today)
        }

        return DailyRecipes(breakfastRecipes, lunchRecipes, dinnerRecipes)
    }

    /**
     * Generate new random recipes and save them to SharedPreferences
     */
    private fun generateAndSaveNewRecipes(context: Context, date: String): DailyRecipes {
        // Generate random recipes
        val breakfastRecipes = RecipeDatabase.getRandomRecipes(MealRecipe.TYPE_BREAKFAST, 3)
        val lunchRecipes = RecipeDatabase.getRandomRecipes(MealRecipe.TYPE_LUNCH, 3)
        val dinnerRecipes = RecipeDatabase.getRandomRecipes(MealRecipe.TYPE_DINNER, 3)

        // Save to SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_LAST_UPDATE_DATE, date)
            putString(KEY_BREAKFAST_IDS, breakfastRecipes.joinToString(",") { it.id })
            putString(KEY_LUNCH_IDS, lunchRecipes.joinToString(",") { it.id })
            putString(KEY_DINNER_IDS, dinnerRecipes.joinToString(",") { it.id })
            apply()
        }

        return DailyRecipes(breakfastRecipes, lunchRecipes, dinnerRecipes)
    }

    /**
     * Force regenerate recipes for today (useful for testing or manual refresh)
     */
    fun refreshTodaysRecipes(context: Context): DailyRecipes {
        val today = dateFormat.format(Calendar.getInstance().time)
        return generateAndSaveNewRecipes(context, today)
    }
}

data class DailyRecipes(
    val breakfast: List<MealRecipe>,
    val lunch: List<MealRecipe>,
    val dinner: List<MealRecipe>
)
