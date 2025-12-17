package com.example.ai_fitness_workout_manager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.ai_fitness_workout_manager.firebase.FirebaseAuthManager
import com.example.ai_fitness_workout_manager.firebase.FirebaseDbManager
import com.example.ai_fitness_workout_manager.model.UserProfile
import com.example.ai_fitness_workout_manager.profile.ProfileQuestion
import com.example.ai_fitness_workout_manager.profile.ProfileQuestionAdapter
import com.example.ai_fitness_workout_manager.profile.ProfileQuestions
import com.google.android.material.progressindicator.LinearProgressIndicator

class UserProfileSetupActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var tvProgress: TextView
    private lateinit var tvSkip: TextView
    private lateinit var btnBack: Button
    private lateinit var btnNext: Button
    private lateinit var loadingOverlay: FrameLayout

    private lateinit var adapter: ProfileQuestionAdapter
    private lateinit var questions: List<ProfileQuestion>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_setup)

        initViews()
        setupQuestions()
        setupViewPager()
        setupClickListeners()
        loadExistingProfile()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPagerQuestions)
        progressIndicator = findViewById(R.id.progressIndicator)
        tvProgress = findViewById(R.id.tvProgress)
        tvSkip = findViewById(R.id.tvSkip)
        btnBack = findViewById(R.id.btnBack)
        btnNext = findViewById(R.id.btnNext)
        loadingOverlay = findViewById(R.id.loadingOverlay)
    }

    private fun setupQuestions() {
        questions = ProfileQuestions.getQuestions()

        adapter = ProfileQuestionAdapter(questions) { fieldName, value ->
            // Optional: Auto-save as user answers
            // For now, we'll batch save at the end
        }
    }

    private fun setupViewPager() {
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false // Disable swipe, use buttons only

        progressIndicator.max = questions.size

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUI(position)
            }
        })

        updateUI(0)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true)
            }
        }

        btnNext.setOnClickListener {
            val currentItem = viewPager.currentItem

            if (currentItem < questions.size - 1) {
                // Validate current question before proceeding
                if (validateCurrentQuestion(currentItem)) {
                    viewPager.setCurrentItem(currentItem + 1, true)
                }
            } else {
                // Last question - save and complete
                if (validateCurrentQuestion(currentItem)) {
                    saveProfileAndNavigate()
                }
            }
        }

        tvSkip.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (!questions[currentItem].isRequired) {
                if (currentItem < questions.size - 1) {
                    viewPager.setCurrentItem(currentItem + 1, true)
                } else {
                    saveProfileAndNavigate()
                }
            }
        }
    }

    private fun updateUI(position: Int) {
        val currentQuestion = questions[position]

        // Update progress
        tvProgress.text = "${position + 1}/${questions.size}"
        progressIndicator.progress = position + 1

        // Update back button visibility
        btnBack.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE

        // Update next button text
        btnNext.text = if (position == questions.size - 1) {
            getString(R.string.complete)
        } else {
            getString(R.string.next)
        }

        // Update skip button visibility
        tvSkip.visibility = if (!currentQuestion.isRequired) View.VISIBLE else View.GONE
    }

    private fun validateCurrentQuestion(position: Int): Boolean {
        val question = questions[position]

        if (!question.isRequired) {
            return true // Optional questions can be skipped
        }

        val answer = adapter.getAnswer(question.fieldName)

        val isValid = when {
            answer == null -> false
            answer is String && answer.trim().isEmpty() -> false
            answer is List<*> && answer.isEmpty() -> false
            else -> true
        }

        if (!isValid) {
            Toast.makeText(
                this,
                getString(R.string.error_please_answer),
                Toast.LENGTH_SHORT
            ).show()
        }

        return isValid
    }

    private fun loadExistingProfile() {
        val userId = FirebaseAuthManager.currentUserId ?: return

        showLoading(true)

        FirebaseDbManager.getUserProfile(userId,
            onSuccess = { profile ->
                showLoading(false)
                profile?.let { prefillAnswers(it) }
            },
            onError = {
                showLoading(false)
                // Continue with empty profile
            }
        )
    }

    private fun prefillAnswers(profile: UserProfile) {
        // Prefill answers from existing profile
        if (profile.fullName.isNotEmpty()) {
            adapter.setAnswer("fullName", profile.fullName)
        }
        if (profile.dateOfBirth.isNotEmpty()) {
            adapter.setAnswer("dateOfBirth", profile.dateOfBirth)
        }
        if (profile.gender.isNotEmpty()) {
            adapter.setAnswer("gender", profile.gender)
        }
        if (profile.heightCm > 0) {
            adapter.setAnswer("heightCm", profile.heightCm)
        }
        if (profile.currentWeightKg > 0) {
            adapter.setAnswer("currentWeightKg", profile.currentWeightKg)
        }
        if (profile.targetWeightKg > 0) {
            adapter.setAnswer("targetWeightKg", profile.targetWeightKg)
        }
        if (profile.fitnessGoals.isNotEmpty()) {
            adapter.setAnswer("fitnessGoals", profile.fitnessGoals)
        }
        if (profile.activityLevel.isNotEmpty()) {
            adapter.setAnswer("activityLevel", profile.activityLevel)
        }
        if (profile.workoutExperience.isNotEmpty()) {
            adapter.setAnswer("workoutExperience", profile.workoutExperience)
        }
        if (profile.preferredWorkouts.isNotEmpty()) {
            adapter.setAnswer("preferredWorkouts", profile.preferredWorkouts)
        }
        if (profile.availableEquipment.isNotEmpty()) {
            adapter.setAnswer("availableEquipment", profile.availableEquipment)
        }
        if (profile.workoutDaysPerWeek > 0) {
            adapter.setAnswer("workoutDaysPerWeek", profile.workoutDaysPerWeek)
        }
        if (profile.workoutDurationMinutes > 0) {
            adapter.setAnswer("workoutDurationMinutes", profile.workoutDurationMinutes)
        }
        if (profile.dietaryPreferences.isNotEmpty()) {
            adapter.setAnswer("dietaryPreferences", profile.dietaryPreferences)
        }
        if (profile.healthConditions.isNotEmpty()) {
            adapter.setAnswer("healthConditions", profile.healthConditions)
        }
        if (profile.sleepHoursPerNight > 0) {
            adapter.setAnswer("sleepHoursPerNight", profile.sleepHoursPerNight)
        }

        // Refresh the adapter
        adapter.notifyDataSetChanged()
    }

    private fun saveProfileAndNavigate() {
        val userId = FirebaseAuthManager.currentUserId
        if (userId == null) {
            Toast.makeText(this, getString(R.string.error_not_logged_in), Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val answers = adapter.getAllAnswers()

        val profile = UserProfile(
            fullName = answers["fullName"] as? String ?: "",
            dateOfBirth = answers["dateOfBirth"] as? String ?: "",
            gender = answers["gender"] as? String ?: "",
            heightCm = (answers["heightCm"] as? Number)?.toInt() ?: 0,
            currentWeightKg = (answers["currentWeightKg"] as? Number)?.toFloat() ?: 0f,
            targetWeightKg = (answers["targetWeightKg"] as? Number)?.toFloat() ?: 0f,
            fitnessGoals = getStringList(answers["fitnessGoals"]),
            activityLevel = answers["activityLevel"] as? String ?: "",
            workoutExperience = answers["workoutExperience"] as? String ?: "",
            preferredWorkouts = getStringList(answers["preferredWorkouts"]),
            availableEquipment = getStringList(answers["availableEquipment"]),
            workoutDaysPerWeek = (answers["workoutDaysPerWeek"] as? Number)?.toInt() ?: 3,
            workoutDurationMinutes = (answers["workoutDurationMinutes"] as? Number)?.toInt() ?: 45,
            dietaryPreferences = getStringList(answers["dietaryPreferences"]),
            healthConditions = getStringList(answers["healthConditions"]),
            sleepHoursPerNight = (answers["sleepHoursPerNight"] as? Number)?.toInt() ?: 7,
            profileCompleted = true,
            updatedAt = System.currentTimeMillis()
        )

        FirebaseDbManager.updateUserProfile(
            userId = userId,
            profile = profile,
            onSuccess = {
                showLoading(false)
                navigateToMain()
            },
            onError = { error ->
                showLoading(false)
                Toast.makeText(
                    this,
                    getString(R.string.error_saving_profile, error),
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun getStringList(value: Any?): List<String> {
        return when (value) {
            is List<*> -> value.filterIsInstance<String>()
            else -> emptyList()
        }
    }

    private fun showLoading(show: Boolean) {
        loadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
        btnNext.isEnabled = !show
        btnBack.isEnabled = !show
    }

    private fun navigateToMain() {
        Toast.makeText(
            this,
            getString(R.string.profile_completed),
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val currentItem = viewPager.currentItem
        if (currentItem > 0) {
            viewPager.setCurrentItem(currentItem - 1, true)
        } else {
            // On first question, show confirmation or go back to auth
            super.onBackPressed()
        }
    }
}
