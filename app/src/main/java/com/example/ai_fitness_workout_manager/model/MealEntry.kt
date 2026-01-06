package com.example.ai_fitness_workout_manager.model

data class MealEntry(
    val id: String = "",
    val name: String = "",
    val portion: String = "",
    val calories: Int = 0,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val fiber: Float = 0f,
    val mealType: String = "",  // breakfast, lunch, dinner, snack
    val time: String = "",
    val timestamp: Long = 0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "portion" to portion,
            "calories" to calories,
            "protein" to protein,
            "carbs" to carbs,
            "fat" to fat,
            "fiber" to fiber,
            "mealType" to mealType,
            "time" to time,
            "timestamp" to timestamp
        )
    }

    companion object {
        const val TYPE_BREAKFAST = "breakfast"
        const val TYPE_LUNCH = "lunch"
        const val TYPE_DINNER = "dinner"
        const val TYPE_SNACK = "snack"

        // Mock data for demonstration
        fun getMockMeals(): List<MealEntry> {
            return listOf(
                MealEntry(
                    id = "1",
                    name = "Oatmeal with Berries",
                    portion = "1 bowl",
                    calories = 350,
                    protein = 12f,
                    carbs = 45f,
                    fat = 8f,
                    fiber = 6f,
                    mealType = TYPE_BREAKFAST,
                    time = "8:30 AM",
                    timestamp = System.currentTimeMillis()
                ),
                MealEntry(
                    id = "2",
                    name = "Grilled Chicken Salad",
                    portion = "1 large plate",
                    calories = 450,
                    protein = 35f,
                    carbs = 20f,
                    fat = 15f,
                    fiber = 8f,
                    mealType = TYPE_LUNCH,
                    time = "12:30 PM",
                    timestamp = System.currentTimeMillis()
                ),
                MealEntry(
                    id = "3",
                    name = "Greek Yogurt with Nuts",
                    portion = "1 cup",
                    calories = 200,
                    protein = 15f,
                    carbs = 12f,
                    fat = 10f,
                    fiber = 2f,
                    mealType = TYPE_SNACK,
                    time = "10:30 AM",
                    timestamp = System.currentTimeMillis()
                ),
                MealEntry(
                    id = "4",
                    name = "Salmon with Vegetables",
                    portion = "200g salmon + veggies",
                    calories = 520,
                    protein = 40f,
                    carbs = 25f,
                    fat = 22f,
                    fiber = 7f,
                    mealType = TYPE_DINNER,
                    time = "7:00 PM",
                    timestamp = System.currentTimeMillis()
                ),
                MealEntry(
                    id = "5",
                    name = "Apple with Peanut Butter",
                    portion = "1 apple + 2 tbsp",
                    calories = 180,
                    protein = 5f,
                    carbs = 25f,
                    fat = 8f,
                    fiber = 4f,
                    mealType = TYPE_SNACK,
                    time = "3:30 PM",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}

data class DailyNutrition(
    val date: String = "",
    val meals: List<MealEntry> = emptyList(),
    val goalCalories: Int = 2200,
    val goalProtein: Float = 150f,
    val goalCarbs: Float = 250f,
    val goalFat: Float = 80f,
    val goalFiber: Float = 30f
) {
    val totalCalories: Int get() = meals.sumOf { it.calories }
    val totalProtein: Float get() = meals.map { it.protein }.sum()
    val totalCarbs: Float get() = meals.map { it.carbs }.sum()
    val totalFat: Float get() = meals.map { it.fat }.sum()
    val totalFiber: Float get() = meals.map { it.fiber }.sum()

    val caloriesProgress: Float get() = (totalCalories.toFloat() / goalCalories).coerceIn(0f, 1f)
    val proteinProgress: Float get() = (totalProtein / goalProtein).coerceIn(0f, 1f)
    val carbsProgress: Float get() = (totalCarbs / goalCarbs).coerceIn(0f, 1f)
    val fatProgress: Float get() = (totalFat / goalFat).coerceIn(0f, 1f)
    val fiberProgress: Float get() = (totalFiber / goalFiber).coerceIn(0f, 1f)
}
