package com.example.ai_fitness_workout_manager.model

data class MealRecipe(
    val id: String = "",
    val name: String = "",
    val mealType: String = "", // breakfast, lunch, dinner
    val description: String = "",
    val prepTime: Int = 0, // minutes
    val cookTime: Int = 0, // minutes
    val servings: Int = 1,
    val difficulty: String = "", // easy, medium, hard
    val calories: Int = 0,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val fiber: Float = 0f,
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<String> = emptyList(),
    val imageUrl: String = "" // For future use
) {
    companion object {
        const val TYPE_BREAKFAST = "breakfast"
        const val TYPE_LUNCH = "lunch"
        const val TYPE_DINNER = "dinner"
    }
}

data class Ingredient(
    val name: String = "",
    val amount: Float = 0f,
    val unit: String = "" // grams, tbsp, tsp, cup, etc.
) {
    fun getDisplayText(): String {
        return "$amount $unit $name"
    }
}
