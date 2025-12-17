package com.example.ai_fitness_workout_manager.profile

enum class QuestionType {
    TEXT_INPUT,
    SINGLE_CHOICE,
    MULTI_CHOICE,
    NUMBER_PICKER,
    SLIDER,
    DATE_PICKER
}

data class ProfileQuestion(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val type: QuestionType,
    val fieldName: String, // Maps to UserProfile field
    val options: List<QuestionOption> = emptyList(),
    val minValue: Int = 0,
    val maxValue: Int = 100,
    val defaultValue: Int = 50,
    val unit: String = "",
    val isRequired: Boolean = true,
    val hint: String = ""
)

data class QuestionOption(
    val id: String,
    val label: String,
    val description: String = "",
    val iconRes: Int = 0
)

object ProfileQuestions {

    fun getQuestions(): List<ProfileQuestion> = listOf(
        // 1. Full Name (might already be filled from sign up)
        ProfileQuestion(
            id = "full_name",
            title = "What's your name?",
            subtitle = "Let's personalize your fitness journey",
            type = QuestionType.TEXT_INPUT,
            fieldName = "fullName",
            hint = "Enter your full name"
        ),

        // 2. Date of Birth
        ProfileQuestion(
            id = "date_of_birth",
            title = "When were you born?",
            subtitle = "We'll use this to customize your workout plans",
            type = QuestionType.DATE_PICKER,
            fieldName = "dateOfBirth"
        ),

        // 3. Gender
        ProfileQuestion(
            id = "gender",
            title = "What's your gender?",
            subtitle = "This helps us personalize your experience",
            type = QuestionType.SINGLE_CHOICE,
            fieldName = "gender",
            options = listOf(
                QuestionOption("male", "Male"),
                QuestionOption("female", "Female"),
                QuestionOption("other", "Other"),
                QuestionOption("prefer_not_to_say", "Prefer not to say")
            )
        ),

        // 4. Height
        ProfileQuestion(
            id = "height",
            title = "What's your height?",
            subtitle = "Slide to select your height",
            type = QuestionType.SLIDER,
            fieldName = "heightCm",
            minValue = 120,
            maxValue = 220,
            defaultValue = 170,
            unit = "cm"
        ),

        // 5. Current Weight
        ProfileQuestion(
            id = "current_weight",
            title = "What's your current weight?",
            subtitle = "Be honest - this is just our starting point!",
            type = QuestionType.SLIDER,
            fieldName = "currentWeightKg",
            minValue = 30,
            maxValue = 200,
            defaultValue = 70,
            unit = "kg"
        ),

        // 6. Target Weight
        ProfileQuestion(
            id = "target_weight",
            title = "What's your target weight?",
            subtitle = "Where do you want to be?",
            type = QuestionType.SLIDER,
            fieldName = "targetWeightKg",
            minValue = 30,
            maxValue = 200,
            defaultValue = 70,
            unit = "kg",
            isRequired = false
        ),

        // 7. Fitness Goals
        ProfileQuestion(
            id = "fitness_goals",
            title = "What are your fitness goals?",
            subtitle = "Select all that apply",
            type = QuestionType.MULTI_CHOICE,
            fieldName = "fitnessGoals",
            options = listOf(
                QuestionOption("lose_fat", "Lose Fat", "Burn calories and reduce body fat"),
                QuestionOption("build_muscle", "Build Muscle", "Gain strength and muscle mass"),
                QuestionOption("maintain_weight", "Maintain Weight", "Stay at your current weight"),
                QuestionOption("improve_endurance", "Improve Endurance", "Boost stamina and cardio"),
                QuestionOption("increase_flexibility", "Increase Flexibility", "Improve mobility and stretch"),
                QuestionOption("general_health", "General Health", "Overall fitness and wellness")
            )
        ),

        // 8. Activity Level
        ProfileQuestion(
            id = "activity_level",
            title = "What's your current activity level?",
            subtitle = "How active are you right now?",
            type = QuestionType.SINGLE_CHOICE,
            fieldName = "activityLevel",
            options = listOf(
                QuestionOption("sedentary", "Sedentary", "Little or no exercise"),
                QuestionOption("lightly_active", "Lightly Active", "Light exercise 1-3 days/week"),
                QuestionOption("moderately_active", "Moderately Active", "Moderate exercise 3-5 days/week"),
                QuestionOption("very_active", "Very Active", "Hard exercise 6-7 days/week"),
                QuestionOption("extra_active", "Extra Active", "Very hard exercise & physical job")
            )
        ),

        // 9. Workout Experience
        ProfileQuestion(
            id = "workout_experience",
            title = "What's your workout experience?",
            subtitle = "No judgment here - everyone starts somewhere!",
            type = QuestionType.SINGLE_CHOICE,
            fieldName = "workoutExperience",
            options = listOf(
                QuestionOption("beginner", "Beginner", "0-6 months of regular exercise"),
                QuestionOption("intermediate", "Intermediate", "6 months - 2 years"),
                QuestionOption("advanced", "Advanced", "2+ years of consistent training")
            )
        ),

        // 10. Preferred Workout Types
        ProfileQuestion(
            id = "preferred_workouts",
            title = "What workouts do you enjoy?",
            subtitle = "Select all that interest you",
            type = QuestionType.MULTI_CHOICE,
            fieldName = "preferredWorkouts",
            options = listOf(
                QuestionOption("strength", "Strength Training", "Weights and resistance"),
                QuestionOption("cardio", "Cardio", "Running, cycling, etc."),
                QuestionOption("hiit", "HIIT", "High intensity intervals"),
                QuestionOption("yoga", "Yoga/Pilates", "Flexibility and mindfulness"),
                QuestionOption("swimming", "Swimming", "Pool workouts"),
                QuestionOption("running", "Running/Jogging", "Outdoor or treadmill"),
                QuestionOption("cycling", "Cycling", "Bike or stationary"),
                QuestionOption("sports", "Sports", "Team or individual sports"),
                QuestionOption("home", "Home Workouts", "Exercise at home"),
                QuestionOption("gym", "Gym Workouts", "Full gym equipment")
            )
        ),

        // 11. Available Equipment
        ProfileQuestion(
            id = "available_equipment",
            title = "What equipment do you have access to?",
            subtitle = "Select all that apply",
            type = QuestionType.MULTI_CHOICE,
            fieldName = "availableEquipment",
            options = listOf(
                QuestionOption("none", "No Equipment", "Bodyweight only"),
                QuestionOption("dumbbells", "Dumbbells", "Free weights"),
                QuestionOption("barbell", "Barbell & Weights", "Olympic or standard"),
                QuestionOption("resistance_bands", "Resistance Bands", "Elastic bands"),
                QuestionOption("pullup_bar", "Pull-up Bar", "Doorway or mounted"),
                QuestionOption("kettlebells", "Kettlebells", "Cast iron bells"),
                QuestionOption("full_gym", "Full Gym Access", "Complete gym facilities"),
                QuestionOption("cardio_machines", "Cardio Machines", "Treadmill, bike, etc.")
            )
        ),

        // 12. Workout Days Per Week
        ProfileQuestion(
            id = "workout_days",
            title = "How many days per week can you workout?",
            subtitle = "Be realistic - consistency is key!",
            type = QuestionType.SLIDER,
            fieldName = "workoutDaysPerWeek",
            minValue = 1,
            maxValue = 7,
            defaultValue = 3,
            unit = "days"
        ),

        // 13. Workout Duration
        ProfileQuestion(
            id = "workout_duration",
            title = "How long can you workout per session?",
            subtitle = "Average time you can dedicate",
            type = QuestionType.SINGLE_CHOICE,
            fieldName = "workoutDurationMinutes",
            options = listOf(
                QuestionOption("15", "15 minutes", "Quick workouts"),
                QuestionOption("30", "30 minutes", "Short sessions"),
                QuestionOption("45", "45 minutes", "Standard sessions"),
                QuestionOption("60", "60 minutes", "Full workouts"),
                QuestionOption("90", "90+ minutes", "Extended training")
            )
        ),

        // 14. Dietary Preferences
        ProfileQuestion(
            id = "dietary_preferences",
            title = "Do you have any dietary preferences?",
            subtitle = "For meal recommendations",
            type = QuestionType.MULTI_CHOICE,
            fieldName = "dietaryPreferences",
            isRequired = false,
            options = listOf(
                QuestionOption("no_restrictions", "No Restrictions", "I eat everything"),
                QuestionOption("vegetarian", "Vegetarian", "No meat"),
                QuestionOption("vegan", "Vegan", "No animal products"),
                QuestionOption("pescatarian", "Pescatarian", "Fish but no meat"),
                QuestionOption("keto", "Keto", "Low carb, high fat"),
                QuestionOption("gluten_free", "Gluten-Free", "No gluten"),
                QuestionOption("dairy_free", "Dairy-Free", "No dairy products"),
                QuestionOption("halal", "Halal", "Halal diet"),
                QuestionOption("kosher", "Kosher", "Kosher diet")
            )
        ),

        // 15. Health Conditions
        ProfileQuestion(
            id = "health_conditions",
            title = "Any health conditions we should know about?",
            subtitle = "This helps us recommend safe exercises",
            type = QuestionType.MULTI_CHOICE,
            fieldName = "healthConditions",
            isRequired = false,
            options = listOf(
                QuestionOption("none", "None", "No health conditions"),
                QuestionOption("back_problems", "Back Problems", "Lower or upper back issues"),
                QuestionOption("knee_issues", "Knee Issues", "Knee pain or injuries"),
                QuestionOption("shoulder_problems", "Shoulder Problems", "Shoulder pain or injuries"),
                QuestionOption("heart_condition", "Heart Condition", "Cardiovascular issues"),
                QuestionOption("diabetes", "Diabetes", "Type 1 or Type 2"),
                QuestionOption("high_blood_pressure", "High Blood Pressure", "Hypertension")
            )
        ),

        // 16. Sleep Hours
        ProfileQuestion(
            id = "sleep_hours",
            title = "How many hours do you sleep per night?",
            subtitle = "Sleep is crucial for recovery",
            type = QuestionType.SLIDER,
            fieldName = "sleepHoursPerNight",
            minValue = 4,
            maxValue = 12,
            defaultValue = 7,
            unit = "hours"
        )
    )
}
