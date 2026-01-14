package com.example.ai_fitness_workout_manager.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.adapter.WorkoutAdapter
import com.example.ai_fitness_workout_manager.ai.GeminiWorkoutManager
import com.example.ai_fitness_workout_manager.firebase.FirebaseAuthManager
import com.example.ai_fitness_workout_manager.firebase.FirebaseDbManager
import com.example.ai_fitness_workout_manager.model.DailyWorkout
import com.example.ai_fitness_workout_manager.model.Exercise
import com.example.ai_fitness_workout_manager.utils.WeeklyWorkoutManager
import kotlinx.coroutines.launch

class WorkoutsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnRefresh: ImageView
    private lateinit var tvWeekTitle: TextView
    private lateinit var rvWorkouts: RecyclerView
    private lateinit var loadingContainer: LinearLayout
    private lateinit var contentContainer: LinearLayout
    private lateinit var errorContainer: LinearLayout
    private lateinit var tvLoadingMessage: TextView
    private lateinit var tvErrorMessage: TextView
    private lateinit var tvErrorDetails: TextView
    private lateinit var btnRetry: Button

    private var workoutPlan: List<DailyWorkout> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        initializeViews()
        setupClickListeners()
        initializeAI()
        loadWorkoutPlan()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        btnRefresh = findViewById(R.id.btnRefresh)
        tvWeekTitle = findViewById(R.id.tvWeekTitle)
        rvWorkouts = findViewById(R.id.rvWorkouts)
        loadingContainer = findViewById(R.id.loadingContainer)
        contentContainer = findViewById(R.id.contentContainer)
        errorContainer = findViewById(R.id.errorContainer)
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)
        tvErrorDetails = findViewById(R.id.tvErrorDetails)
        btnRetry = findViewById(R.id.btnRetry)

        // Set week title
        tvWeekTitle.text = WeeklyWorkoutManager.getWeekTitle()

        // Setup RecyclerView
        rvWorkouts.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnRefresh.setOnClickListener {
            showRefreshConfirmationDialog()
        }

        btnRetry.setOnClickListener {
            loadWorkoutPlan()
        }
    }

    private fun initializeAI() {
        if (!GeminiWorkoutManager.isInitialized()) {
            GeminiWorkoutManager.initialize()
        }
    }

    private fun loadWorkoutPlan() {
        // Check if we have a cached plan for this week
        val cachedPlan = WeeklyWorkoutManager.getWorkoutPlan(this)

        if (cachedPlan != null) {
            // Parse and display cached plan
            parseAndDisplayWorkoutPlan(cachedPlan)
        } else {
            // Generate new plan
            generateNewWorkoutPlan()
        }
    }

    private fun generateNewWorkoutPlan() {
        if (!GeminiWorkoutManager.hasValidApiKey()) {
            showError(
                "API Key Not Configured",
                "Please configure your Gemini API key in local.properties to use AI workout generation."
            )
            return
        }

        showLoading("Generating your personalized workout plan...")

        // Get user profile from Firebase
        val userId = FirebaseAuthManager.currentUserId
        if (userId == null) {
            showError("Not Logged In", "Please log in to generate workout plans.")
            return
        }

        FirebaseDbManager.getUserProfile(userId,
            onSuccess = { profile ->
                if (profile == null) {
                    showError("Profile Not Found", "Please complete your profile setup first.")
                    return@getUserProfile
                }

                // Generate workout plan with AI
                lifecycleScope.launch {
                    val result = GeminiWorkoutManager.generateWeeklyWorkoutPlan(profile)

                    result.onSuccess { planText ->
                        // Save to cache
                        WeeklyWorkoutManager.saveWorkoutPlan(this@WorkoutsActivity, planText)
                        // Parse and display
                        parseAndDisplayWorkoutPlan(planText)
                    }.onFailure { error ->
                        showError("Generation Failed", error.message ?: "Unknown error occurred")
                    }
                }
            },
            onError = { error ->
                showError("Profile Load Failed", "Could not load your profile: $error")
            }
        )
    }

    private fun parseAndDisplayWorkoutPlan(planText: String) {
        try {
            val parsedWorkouts = parseWorkoutPlan(planText)

            if (parsedWorkouts.isEmpty()) {
                showError("Parsing Failed", "Could not parse the workout plan. Please try regenerating.")
                return
            }

            workoutPlan = parsedWorkouts
            displayWorkouts()
        } catch (e: Exception) {
            showError("Parsing Error", "Error parsing workout plan: ${e.message}")
        }
    }

    private fun parseWorkoutPlan(planText: String): List<DailyWorkout> {
        val workouts = mutableListOf<DailyWorkout>()
        val days = planText.split("---").map { it.trim() }.filter { it.isNotEmpty() }

        for (dayText in days) {
            try {
                val lines = dayText.lines().map { it.trim() }.filter { it.isNotEmpty() }
                if (lines.isEmpty()) continue

                var dayName = ""
                var workoutType = ""
                var duration = 0
                var difficulty = ""
                var targetMuscles = listOf<String>()
                val exercises = mutableListOf<Exercise>()

                var i = 0
                while (i < lines.size) {
                    val line = lines[i]

                    when {
                        line.startsWith("DAY:", ignoreCase = true) -> {
                            dayName = line.substringAfter(":").trim()
                        }
                        line.startsWith("TYPE:", ignoreCase = true) -> {
                            workoutType = line.substringAfter(":").trim()
                        }
                        line.startsWith("DURATION:", ignoreCase = true) -> {
                            duration = line.substringAfter(":").trim().toIntOrNull() ?: 0
                        }
                        line.startsWith("DIFFICULTY:", ignoreCase = true) -> {
                            difficulty = line.substringAfter(":").trim()
                        }
                        line.startsWith("TARGET:", ignoreCase = true) -> {
                            targetMuscles = line.substringAfter(":").trim()
                                .split(",").map { it.trim() }
                        }
                        line.startsWith("EXERCISES:", ignoreCase = true) -> {
                            // Parse exercises
                            i++
                            while (i < lines.size && !lines[i].startsWith("DAY:", ignoreCase = true)) {
                                val exerciseLine = lines[i]
                                if (exerciseLine.matches(Regex("^\\d+\\.\\s+.*"))) {
                                    // Exercise name
                                    val exerciseName = exerciseLine.substringAfter(". ").trim()
                                    var sets = 3
                                    var reps = "10-12"
                                    var rest = 60
                                    var notes = ""

                                    // Parse exercise details
                                    i++
                                    while (i < lines.size && lines[i].startsWith("-")) {
                                        // Check if next line is a new exercise (break if so)
                                        if (i + 1 < lines.size && lines[i + 1].matches(Regex("^\\d+\\.\\s+.*"))) {
                                            break
                                        }

                                        val detail = lines[i].substringAfter("-").trim()
                                        when {
                                            detail.startsWith("Sets:", ignoreCase = true) -> {
                                                sets = detail.substringAfter(":").trim().toIntOrNull() ?: 3
                                            }
                                            detail.startsWith("Reps:", ignoreCase = true) -> {
                                                reps = detail.substringAfter(":").trim()
                                            }
                                            detail.startsWith("Rest:", ignoreCase = true) -> {
                                                rest = detail.substringAfter(":").trim().toIntOrNull() ?: 60
                                            }
                                            detail.startsWith("Notes:", ignoreCase = true) -> {
                                                notes = detail.substringAfter(":").trim()
                                            }
                                        }
                                        i++
                                        if (i >= lines.size) break
                                    }

                                    exercises.add(Exercise(
                                        name = exerciseName,
                                        sets = sets,
                                        reps = reps,
                                        restTime = rest,
                                        notes = notes,
                                        muscleGroup = targetMuscles.firstOrNull() ?: ""
                                    ))
                                    i--
                                } else {
                                    i++
                                }
                            }
                            i--
                        }
                    }
                    i++
                }

                // Create DailyWorkout object
                if (dayName.isNotEmpty()) {
                    val isRestDay = workoutType.equals("Rest", ignoreCase = true)
                    workouts.add(DailyWorkout(
                        dayOfWeek = dayName,
                        workoutType = workoutType,
                        duration = duration,
                        difficulty = difficulty,
                        exerciseCount = exercises.size,
                        targetMuscles = targetMuscles,
                        exercises = exercises,
                        isRestDay = isRestDay
                    ))
                }
            } catch (e: Exception) {
                // Skip this day if parsing fails
                continue
            }
        }

        return workouts
    }

    private fun displayWorkouts() {
        val adapter = WorkoutAdapter(workoutPlan) { workout ->
            showWorkoutDetails(workout)
        }
        rvWorkouts.adapter = adapter
        showContent()
    }

    private fun showWorkoutDetails(workout: DailyWorkout) {
        // Build exercise details text
        val exerciseDetails = StringBuilder()

        if (!workout.isRestDay) {
            workout.exercises.forEachIndexed { index, exercise ->
                exerciseDetails.append("${index + 1}. ${exercise.name}\n")
                exerciseDetails.append("   • Sets: ${exercise.sets}\n")
                exerciseDetails.append("   • Reps: ${exercise.reps}\n")
                exerciseDetails.append("   • Rest: ${exercise.restTime}s between sets\n")
                if (exercise.notes.isNotEmpty()) {
                    exerciseDetails.append("   • Tip: ${exercise.notes}\n")
                }
                exerciseDetails.append("\n")
            }
        }

        // Launch WorkoutDetailsActivity
        val intent = Intent(this, WorkoutDetailsActivity::class.java).apply {
            putExtra(WorkoutDetailsActivity.EXTRA_WORKOUT_DAY, workout.dayOfWeek)
            putExtra(WorkoutDetailsActivity.EXTRA_WORKOUT_TYPE, workout.workoutType)
            putExtra(WorkoutDetailsActivity.EXTRA_WORKOUT_DURATION, workout.duration)
            putExtra(WorkoutDetailsActivity.EXTRA_WORKOUT_DIFFICULTY, workout.difficulty)
            putExtra(WorkoutDetailsActivity.EXTRA_WORKOUT_TARGET, workout.targetMuscles.joinToString(", "))
            putExtra(WorkoutDetailsActivity.EXTRA_WORKOUT_EXERCISES, exerciseDetails.toString())
            putExtra(WorkoutDetailsActivity.EXTRA_IS_REST_DAY, workout.isRestDay)
        }
        startActivity(intent)
    }

    private fun showRefreshConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Generate New Plan?")
            .setMessage("This will replace your current workout plan with a fresh one. Continue?")
            .setPositiveButton("Generate") { _, _ ->
                WeeklyWorkoutManager.clearWorkoutPlan(this)
                generateNewWorkoutPlan()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLoading(message: String) {
        tvLoadingMessage.text = message
        loadingContainer.visibility = View.VISIBLE
        contentContainer.visibility = View.GONE
        errorContainer.visibility = View.GONE
    }

    private fun showContent() {
        loadingContainer.visibility = View.GONE
        contentContainer.visibility = View.VISIBLE
        errorContainer.visibility = View.GONE
    }

    private fun showError(title: String, message: String) {
        tvErrorMessage.text = title
        tvErrorDetails.text = message
        loadingContainer.visibility = View.GONE
        contentContainer.visibility = View.GONE
        errorContainer.visibility = View.VISIBLE
    }
}
