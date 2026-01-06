package com.example.ai_fitness_workout_manager.firebase

import com.example.ai_fitness_workout_manager.model.MealEntry
import com.example.ai_fitness_workout_manager.model.UserProfile
import com.example.ai_fitness_workout_manager.model.WeightEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object FirebaseDbManager {

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://ai-fitness-tracker-manager-default-rtdb.firebaseio.com/")
    }

    private val usersRef by lazy { database.getReference("users") }

    fun createUserProfile(
        userId: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val initialProfile = UserProfile(
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val userData = mapOf(
            "email" to email,
            "profile" to initialProfile.toMap()
        )

        usersRef.child(userId).setValue(userData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to create user profile") }
    }

    fun updateUserProfile(
        userId: String,
        profile: UserProfile,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val updatedProfile = profile.copy(updatedAt = System.currentTimeMillis())

        usersRef.child(userId).child("profile").setValue(updatedProfile.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to update profile") }
    }

    fun getUserProfile(
        userId: String,
        onSuccess: (UserProfile?) -> Unit,
        onError: (String) -> Unit
    ) {
        usersRef.child(userId).child("profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profile = snapshot.getValue(UserProfile::class.java)
                    onSuccess(profile)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    fun isProfileCompleted(
        userId: String,
        onResult: (Boolean) -> Unit
    ) {
        usersRef.child(userId).child("profile").child("profileCompleted")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isCompleted = snapshot.getValue(Boolean::class.java) ?: false
                    onResult(isCompleted)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(false)
                }
            })
    }

    fun markProfileAsCompleted(
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val updates = mapOf(
            "profileCompleted" to true,
            "updatedAt" to System.currentTimeMillis()
        )

        usersRef.child(userId).child("profile").updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to update profile") }
    }

    fun updateProfileField(
        userId: String,
        fieldName: String,
        value: Any,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val updates = mapOf(
            fieldName to value,
            "updatedAt" to System.currentTimeMillis()
        )

        usersRef.child(userId).child("profile").updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to update field") }
    }

    // ==================== MEAL FUNCTIONS ====================

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Add a new meal for a user on a specific date
     */
    fun addMeal(
        userId: String,
        meal: MealEntry,
        date: String, // Format: "yyyy-MM-dd"
        onSuccess: (String) -> Unit, // Returns the meal ID
        onError: (String) -> Unit
    ) {
        val mealsRef = usersRef.child(userId).child("meals").child(date)
        val newMealRef = mealsRef.push()
        val mealId = newMealRef.key ?: return onError("Failed to generate meal ID")

        val mealWithId = meal.copy(id = mealId)
        newMealRef.setValue(mealWithId.toMap())
            .addOnSuccessListener { onSuccess(mealId) }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to add meal") }
    }

    /**
     * Get all meals for a user on a specific date
     */
    fun getMealsForDate(
        userId: String,
        date: String, // Format: "yyyy-MM-dd"
        onSuccess: (List<MealEntry>) -> Unit,
        onError: (String) -> Unit
    ) {
        usersRef.child(userId).child("meals").child(date)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val meals = mutableListOf<MealEntry>()
                    for (mealSnapshot in snapshot.children) {
                        val meal = mealSnapshot.getValue(MealEntry::class.java)
                        if (meal != null) {
                            meals.add(meal)
                        }
                    }
                    // Sort by timestamp
                    meals.sortBy { it.timestamp }
                    onSuccess(meals)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    /**
     * Update an existing meal
     */
    fun updateMeal(
        userId: String,
        date: String,
        mealId: String,
        meal: MealEntry,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        usersRef.child(userId).child("meals").child(date).child(mealId)
            .setValue(meal.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to update meal") }
    }

    /**
     * Delete a meal
     */
    fun deleteMeal(
        userId: String,
        date: String,
        mealId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        usersRef.child(userId).child("meals").child(date).child(mealId)
            .removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to delete meal") }
    }

    /**
     * Populate sample meals for the last 7 days (for demo/testing)
     */
    fun populateSampleMeals(
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val updates = mutableMapOf<String, Any>()

        // Sample meal templates
        val breakfastOptions = listOf(
            MealEntry(name = "Oatmeal with Berries", portion = "1 bowl", calories = 350, protein = 12f, carbs = 45f, fat = 8f, fiber = 6f, mealType = MealEntry.TYPE_BREAKFAST, time = "8:30 AM"),
            MealEntry(name = "Scrambled Eggs with Toast", portion = "2 eggs + 2 slices", calories = 380, protein = 20f, carbs = 30f, fat = 18f, fiber = 2f, mealType = MealEntry.TYPE_BREAKFAST, time = "8:00 AM"),
            MealEntry(name = "Greek Yogurt Parfait", portion = "1 large", calories = 320, protein = 18f, carbs = 40f, fat = 10f, fiber = 4f, mealType = MealEntry.TYPE_BREAKFAST, time = "9:00 AM"),
            MealEntry(name = "Avocado Toast", portion = "2 slices", calories = 400, protein = 10f, carbs = 35f, fat = 25f, fiber = 8f, mealType = MealEntry.TYPE_BREAKFAST, time = "8:15 AM"),
            MealEntry(name = "Protein Smoothie Bowl", portion = "1 bowl", calories = 420, protein = 25f, carbs = 50f, fat = 12f, fiber = 7f, mealType = MealEntry.TYPE_BREAKFAST, time = "7:45 AM")
        )

        val lunchOptions = listOf(
            MealEntry(name = "Grilled Chicken Salad", portion = "1 large plate", calories = 450, protein = 35f, carbs = 20f, fat = 15f, fiber = 8f, mealType = MealEntry.TYPE_LUNCH, time = "12:30 PM"),
            MealEntry(name = "Turkey Sandwich", portion = "1 sandwich", calories = 480, protein = 28f, carbs = 45f, fat = 18f, fiber = 4f, mealType = MealEntry.TYPE_LUNCH, time = "1:00 PM"),
            MealEntry(name = "Quinoa Buddha Bowl", portion = "1 bowl", calories = 520, protein = 18f, carbs = 60f, fat = 22f, fiber = 12f, mealType = MealEntry.TYPE_LUNCH, time = "12:00 PM"),
            MealEntry(name = "Tuna Wrap", portion = "1 large wrap", calories = 420, protein = 30f, carbs = 35f, fat = 16f, fiber = 5f, mealType = MealEntry.TYPE_LUNCH, time = "12:45 PM"),
            MealEntry(name = "Chicken Stir Fry", portion = "1 plate", calories = 480, protein = 32f, carbs = 40f, fat = 18f, fiber = 6f, mealType = MealEntry.TYPE_LUNCH, time = "1:15 PM")
        )

        val dinnerOptions = listOf(
            MealEntry(name = "Salmon with Vegetables", portion = "200g salmon + veggies", calories = 520, protein = 40f, carbs = 25f, fat = 22f, fiber = 7f, mealType = MealEntry.TYPE_DINNER, time = "7:00 PM"),
            MealEntry(name = "Grilled Steak with Potatoes", portion = "250g steak + sides", calories = 650, protein = 45f, carbs = 35f, fat = 30f, fiber = 4f, mealType = MealEntry.TYPE_DINNER, time = "7:30 PM"),
            MealEntry(name = "Pasta Primavera", portion = "1 large plate", calories = 580, protein = 18f, carbs = 75f, fat = 20f, fiber = 8f, mealType = MealEntry.TYPE_DINNER, time = "6:45 PM"),
            MealEntry(name = "Chicken Breast with Rice", portion = "200g chicken + rice", calories = 550, protein = 42f, carbs = 50f, fat = 12f, fiber = 3f, mealType = MealEntry.TYPE_DINNER, time = "7:15 PM"),
            MealEntry(name = "Shrimp Tacos", portion = "3 tacos", calories = 480, protein = 28f, carbs = 40f, fat = 22f, fiber = 5f, mealType = MealEntry.TYPE_DINNER, time = "6:30 PM")
        )

        val snackOptions = listOf(
            MealEntry(name = "Greek Yogurt with Nuts", portion = "1 cup", calories = 200, protein = 15f, carbs = 12f, fat = 10f, fiber = 2f, mealType = MealEntry.TYPE_SNACK, time = "10:30 AM"),
            MealEntry(name = "Apple with Peanut Butter", portion = "1 apple + 2 tbsp", calories = 180, protein = 5f, carbs = 25f, fat = 8f, fiber = 4f, mealType = MealEntry.TYPE_SNACK, time = "3:30 PM"),
            MealEntry(name = "Protein Bar", portion = "1 bar", calories = 220, protein = 20f, carbs = 22f, fat = 8f, fiber = 3f, mealType = MealEntry.TYPE_SNACK, time = "4:00 PM"),
            MealEntry(name = "Mixed Nuts", portion = "1/4 cup", calories = 170, protein = 5f, carbs = 8f, fat = 15f, fiber = 2f, mealType = MealEntry.TYPE_SNACK, time = "11:00 AM"),
            MealEntry(name = "Banana with Almond Butter", portion = "1 banana + 1 tbsp", calories = 190, protein = 4f, carbs = 28f, fat = 8f, fiber = 3f, mealType = MealEntry.TYPE_SNACK, time = "3:00 PM"),
            MealEntry(name = "Cottage Cheese with Fruit", portion = "1 cup", calories = 160, protein = 14f, carbs = 15f, fat = 4f, fiber = 1f, mealType = MealEntry.TYPE_SNACK, time = "10:00 AM")
        )

        // Generate meals for last 7 days
        for (dayOffset in 0..6) {
            calendar.time = java.util.Date()
            calendar.add(Calendar.DAY_OF_YEAR, -dayOffset)
            val dateStr = dateFormat.format(calendar.time)
            val dayTimestamp = calendar.timeInMillis

            val dayMeals = mutableMapOf<String, Any>()

            // Add breakfast
            val breakfast = breakfastOptions[dayOffset % breakfastOptions.size]
                .copy(id = "breakfast_$dayOffset", timestamp = dayTimestamp + 8 * 3600000)
            dayMeals["breakfast_$dayOffset"] = breakfast.toMap()

            // Add lunch
            val lunch = lunchOptions[dayOffset % lunchOptions.size]
                .copy(id = "lunch_$dayOffset", timestamp = dayTimestamp + 12 * 3600000)
            dayMeals["lunch_$dayOffset"] = lunch.toMap()

            // Add dinner
            val dinner = dinnerOptions[dayOffset % dinnerOptions.size]
                .copy(id = "dinner_$dayOffset", timestamp = dayTimestamp + 19 * 3600000)
            dayMeals["dinner_$dayOffset"] = dinner.toMap()

            // Add 1-2 snacks
            val snack1 = snackOptions[dayOffset % snackOptions.size]
                .copy(id = "snack1_$dayOffset", timestamp = dayTimestamp + 10 * 3600000)
            dayMeals["snack1_$dayOffset"] = snack1.toMap()

            if (dayOffset % 2 == 0) { // Add second snack every other day
                val snack2 = snackOptions[(dayOffset + 1) % snackOptions.size]
                    .copy(id = "snack2_$dayOffset", timestamp = dayTimestamp + 15 * 3600000)
                dayMeals["snack2_$dayOffset"] = snack2.toMap()
            }

            updates["meals/$dateStr"] = dayMeals
        }

        usersRef.child(userId).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to populate meals") }
    }

    /**
     * Format a Date to string for database keys
     */
    fun formatDateForDb(date: java.util.Date): String {
        return dateFormat.format(date)
    }

    // ==================== WEIGHT TRACKING FUNCTIONS ====================

    /**
     * Add a new weight entry and update current weight in profile
     */
    fun addWeightEntry(
        userId: String,
        weightKg: Float,
        note: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val today = formatDateForDb(java.util.Date())
        val weightRef = usersRef.child(userId).child("weightHistory").child(today)
        val entryId = weightRef.push().key ?: return onError("Failed to generate entry ID")

        val entry = WeightEntry(
            id = entryId,
            weightKg = weightKg,
            date = today,
            timestamp = System.currentTimeMillis(),
            note = note
        )

        // Update both weight history and current weight in profile
        val updates = mapOf(
            "weightHistory/$today/$entryId" to entry.toMap(),
            "profile/currentWeightKg" to weightKg,
            "profile/updatedAt" to System.currentTimeMillis()
        )

        usersRef.child(userId).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to add weight entry") }
    }

    /**
     * Get all weight entries for a user (sorted by date)
     */
    fun getWeightHistory(
        userId: String,
        onSuccess: (List<WeightEntry>) -> Unit,
        onError: (String) -> Unit
    ) {
        usersRef.child(userId).child("weightHistory")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = mutableListOf<WeightEntry>()
                    for (dateSnapshot in snapshot.children) {
                        for (entrySnapshot in dateSnapshot.children) {
                            val entry = entrySnapshot.getValue(WeightEntry::class.java)
                            if (entry != null) {
                                entries.add(entry)
                            }
                        }
                    }
                    // Sort by timestamp (oldest first for graph)
                    entries.sortBy { it.timestamp }
                    onSuccess(entries)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    /**
     * Get the latest weight entry
     */
    fun getLatestWeight(
        userId: String,
        onSuccess: (WeightEntry?) -> Unit,
        onError: (String) -> Unit
    ) {
        usersRef.child(userId).child("weightHistory")
            .orderByKey()
            .limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var latestEntry: WeightEntry? = null
                    var latestTimestamp = 0L

                    for (dateSnapshot in snapshot.children) {
                        for (entrySnapshot in dateSnapshot.children) {
                            val entry = entrySnapshot.getValue(WeightEntry::class.java)
                            if (entry != null && entry.timestamp > latestTimestamp) {
                                latestEntry = entry
                                latestTimestamp = entry.timestamp
                            }
                        }
                    }
                    onSuccess(latestEntry)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    /**
     * Populate sample weight history for demo (simulating gradual weight loss over past 30 days)
     */
    fun populateSampleWeightHistory(
        userId: String,
        startWeight: Float,
        targetWeight: Float,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val updates = mutableMapOf<String, Any>()

        val totalDays = 30
        val weightDiff = startWeight - targetWeight
        val dailyChange = weightDiff / 60f // Simulate losing weight over 60 days (halfway through)

        // Generate weight entries for last 30 days
        for (dayOffset in totalDays downTo 0) {
            calendar.time = java.util.Date()
            calendar.add(Calendar.DAY_OF_YEAR, -dayOffset)
            val dateStr = dateFormat.format(calendar.time)

            // Add some realistic variation (+/- 0.3kg)
            val variation = (Math.random() * 0.6 - 0.3).toFloat()
            val weight = startWeight - (dailyChange * (totalDays - dayOffset)) + variation

            val entryId = "weight_$dayOffset"
            val entry = WeightEntry(
                id = entryId,
                weightKg = String.format("%.1f", weight).toFloat(),
                date = dateStr,
                timestamp = calendar.timeInMillis,
                note = if (dayOffset == totalDays) "Starting weight" else ""
            )

            updates["weightHistory/$dateStr/$entryId"] = entry.toMap()
        }

        usersRef.child(userId).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to populate weight history") }
    }
}
