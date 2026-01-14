package com.example.ai_fitness_workout_manager.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.model.DailyWorkout

class WorkoutDetailsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnCopy: ImageView
    private lateinit var tvWorkoutTitle: TextView
    private lateinit var tvWorkoutDetails: TextView

    companion object {
        const val EXTRA_WORKOUT_DAY = "extra_workout_day"
        const val EXTRA_WORKOUT_TYPE = "extra_workout_type"
        const val EXTRA_WORKOUT_DURATION = "extra_workout_duration"
        const val EXTRA_WORKOUT_DIFFICULTY = "extra_workout_difficulty"
        const val EXTRA_WORKOUT_TARGET = "extra_workout_target"
        const val EXTRA_WORKOUT_EXERCISES = "extra_workout_exercises"
        const val EXTRA_IS_REST_DAY = "extra_is_rest_day"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_details)

        initializeViews()
        setupClickListeners()
        displayWorkoutDetails()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        btnCopy = findViewById(R.id.btnCopy)
        tvWorkoutTitle = findViewById(R.id.tvWorkoutTitle)
        tvWorkoutDetails = findViewById(R.id.tvWorkoutDetails)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCopy.setOnClickListener {
            copyWorkoutToClipboard()
        }
    }

    private fun displayWorkoutDetails() {
        val dayName = intent.getStringExtra(EXTRA_WORKOUT_DAY) ?: "Workout"
        val isRestDay = intent.getBooleanExtra(EXTRA_IS_REST_DAY, false)

        tvWorkoutTitle.text = "$dayName Workout"

        if (isRestDay) {
            tvWorkoutDetails.text = buildRestDayText()
        } else {
            val workoutType = intent.getStringExtra(EXTRA_WORKOUT_TYPE) ?: ""
            val duration = intent.getIntExtra(EXTRA_WORKOUT_DURATION, 0)
            val difficulty = intent.getStringExtra(EXTRA_WORKOUT_DIFFICULTY) ?: ""
            val target = intent.getStringExtra(EXTRA_WORKOUT_TARGET) ?: ""
            val exercisesText = intent.getStringExtra(EXTRA_WORKOUT_EXERCISES) ?: ""

            tvWorkoutDetails.text = buildWorkoutDetailsText(
                workoutType, duration, difficulty, target, exercisesText
            )
        }
    }

    private fun buildRestDayText(): String {
        return """
            REST DAY

            Today is a rest day - an essential part of your fitness journey!

            WHY REST DAYS MATTER:
            • Muscle Recovery: Your muscles need time to repair and grow stronger
            • Prevent Injury: Adequate rest reduces risk of overuse injuries
            • Mental Refresh: Take a break from intense training
            • Better Performance: You'll come back stronger for your next workout

            WHAT TO DO TODAY:
            ✓ Light stretching or yoga (optional)
            ✓ Stay hydrated
            ✓ Eat nutritious meals
            ✓ Get quality sleep
            ✓ Relax and enjoy your day off

            Remember: Rest is not laziness - it's a crucial part of getting fit!
        """.trimIndent()
    }

    private fun buildWorkoutDetailsText(
        workoutType: String,
        duration: Int,
        difficulty: String,
        target: String,
        exercisesText: String
    ): String {
        val details = StringBuilder()

        details.append("═══════════════════════════════════════\n")
        details.append("  $workoutType\n")
        details.append("═══════════════════════════════════════\n\n")

        details.append("📊 WORKOUT OVERVIEW\n")
        details.append("─────────────────────────────────────\n")
        details.append("Duration:    $duration minutes\n")
        details.append("Difficulty:  $difficulty\n")
        details.append("Target:      $target\n")
        details.append("\n")

        details.append("💪 EXERCISES\n")
        details.append("─────────────────────────────────────\n\n")
        details.append(exercisesText)

        details.append("\n\n")
        details.append("═══════════════════════════════════════\n")
        details.append("  TIPS FOR SUCCESS\n")
        details.append("═══════════════════════════════════════\n\n")
        details.append("✓ Warm up before starting\n")
        details.append("✓ Focus on proper form over speed\n")
        details.append("✓ Take rest periods between sets\n")
        details.append("✓ Stay hydrated throughout\n")
        details.append("✓ Cool down and stretch after\n")
        details.append("✓ Listen to your body\n")

        return details.toString()
    }

    private fun copyWorkoutToClipboard() {
        val workoutText = tvWorkoutDetails.text.toString()

        if (workoutText.isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Workout Details", workoutText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show()
        }
    }
}
