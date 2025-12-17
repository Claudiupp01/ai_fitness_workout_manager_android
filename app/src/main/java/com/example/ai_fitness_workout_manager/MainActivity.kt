package com.example.ai_fitness_workout_manager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_fitness_workout_manager.firebase.FirebaseAuthManager
import com.example.ai_fitness_workout_manager.firebase.FirebaseDbManager

class MainActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvUserInfo: TextView
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        loadUserData()
        setupClickListeners()
    }

    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        tvUserInfo = findViewById(R.id.tvUserInfo)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun loadUserData() {
        val userId = FirebaseAuthManager.currentUserId

        if (userId == null) {
            navigateToAuth()
            return
        }

        FirebaseDbManager.getUserProfile(userId,
            onSuccess = { profile ->
                profile?.let {
                    tvWelcome.text = getString(R.string.welcome_user, it.fullName.ifEmpty { "User" })

                    val info = buildString {
                        if (it.fitnessGoals.isNotEmpty()) {
                            append("Goals: ${formatList(it.fitnessGoals)}\n")
                        }
                        if (it.activityLevel.isNotEmpty()) {
                            append("Activity: ${formatActivityLevel(it.activityLevel)}\n")
                        }
                        if (it.workoutDaysPerWeek > 0) {
                            append("Workout days: ${it.workoutDaysPerWeek}/week\n")
                        }
                        if (it.currentWeightKg > 0 && it.targetWeightKg > 0) {
                            append("Weight: ${it.currentWeightKg.toInt()}kg → ${it.targetWeightKg.toInt()}kg")
                        }
                    }
                    tvUserInfo.text = info.ifEmpty { getString(R.string.dashboard_placeholder) }
                } ?: run {
                    tvWelcome.text = getString(R.string.welcome_user, "User")
                    tvUserInfo.text = getString(R.string.dashboard_placeholder)
                }
            },
            onError = {
                tvWelcome.text = getString(R.string.welcome_user, "User")
                tvUserInfo.text = getString(R.string.dashboard_placeholder)
            }
        )
    }

    private fun formatList(items: List<String>): String {
        return items.joinToString(", ") { it.replace("_", " ").capitalizeWords() }
    }

    private fun formatActivityLevel(level: String): String {
        return level.replace("_", " ").capitalizeWords()
    }

    private fun String.capitalizeWords(): String {
        return split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    private fun setupClickListeners() {
        btnLogout.setOnClickListener {
            FirebaseAuthManager.signOut()
            navigateToAuth()
        }
    }

    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
