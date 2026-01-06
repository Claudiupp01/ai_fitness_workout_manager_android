package com.example.ai_fitness_workout_manager.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ai_fitness_workout_manager.AuthActivity
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.firebase.FirebaseAuthManager
import com.example.ai_fitness_workout_manager.firebase.FirebaseDbManager
import com.example.ai_fitness_workout_manager.model.UserProfile
import com.example.ai_fitness_workout_manager.model.WeightEntry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var tvInitials: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserBasicInfo: TextView
    private lateinit var tvCurrentWeight: TextView
    private lateinit var tvTargetWeight: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeightChange: TextView
    private lateinit var tvProgressSubtitle: TextView
    private lateinit var weightChart: LineChart
    private lateinit var btnUpdateWeight: Button
    private lateinit var chipGroupGoals: ChipGroup
    private lateinit var tvActivityLevel: TextView
    private lateinit var tvExperience: TextView
    private lateinit var tvWorkoutDays: TextView
    private lateinit var tvWorkoutDuration: TextView
    private lateinit var tvSleepHours: TextView
    private lateinit var tvBmi: TextView
    private lateinit var chipGroupDietary: ChipGroup
    private lateinit var btnLogout: Button

    private var currentProfile: UserProfile? = null
    private var weightHistory: List<WeightEntry> = emptyList()

    companion object {
        private const val PREFS_NAME = "fitness_app_prefs"
        private const val KEY_WEIGHT_HISTORY_POPULATED = "weight_history_populated"

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupClickListeners()
        loadUserProfile()
    }

    private fun initViews(view: View) {
        tvInitials = view.findViewById(R.id.tvInitials)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserBasicInfo = view.findViewById(R.id.tvUserBasicInfo)
        tvCurrentWeight = view.findViewById(R.id.tvCurrentWeight)
        tvTargetWeight = view.findViewById(R.id.tvTargetWeight)
        tvHeight = view.findViewById(R.id.tvHeight)
        tvWeightChange = view.findViewById(R.id.tvWeightChange)
        tvProgressSubtitle = view.findViewById(R.id.tvProgressSubtitle)
        weightChart = view.findViewById(R.id.weightChart)
        btnUpdateWeight = view.findViewById(R.id.btnUpdateWeight)
        chipGroupGoals = view.findViewById(R.id.chipGroupGoals)
        tvActivityLevel = view.findViewById(R.id.tvActivityLevel)
        tvExperience = view.findViewById(R.id.tvExperience)
        tvWorkoutDays = view.findViewById(R.id.tvWorkoutDays)
        tvWorkoutDuration = view.findViewById(R.id.tvWorkoutDuration)
        tvSleepHours = view.findViewById(R.id.tvSleepHours)
        tvBmi = view.findViewById(R.id.tvBmi)
        chipGroupDietary = view.findViewById(R.id.chipGroupDietary)
        btnLogout = view.findViewById(R.id.btnLogout)
    }

    private fun setupClickListeners() {
        btnUpdateWeight.setOnClickListener {
            showUpdateWeightDialog()
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun loadUserProfile() {
        val userId = FirebaseAuthManager.currentUserId ?: return

        FirebaseDbManager.getUserProfile(userId,
            onSuccess = { profile ->
                currentProfile = profile
                profile?.let {
                    updateUI(it)
                    checkAndPopulateWeightHistory(it)
                }
            },
            onError = { error ->
                Toast.makeText(context, "Failed to load profile: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun checkAndPopulateWeightHistory(profile: UserProfile) {
        val userId = FirebaseAuthManager.currentUserId ?: return
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = "${KEY_WEIGHT_HISTORY_POPULATED}_$userId"

        if (!prefs.getBoolean(key, false) && profile.currentWeightKg > 0) {
            // Populate sample weight history
            val startWeight = profile.currentWeightKg + 3f // Simulate starting 3kg higher
            FirebaseDbManager.populateSampleWeightHistory(
                userId = userId,
                startWeight = startWeight,
                targetWeight = profile.targetWeightKg,
                onSuccess = {
                    prefs.edit().putBoolean(key, true).apply()
                    loadWeightHistory()
                },
                onError = {
                    loadWeightHistory()
                }
            )
        } else {
            loadWeightHistory()
        }
    }

    private fun loadWeightHistory() {
        val userId = FirebaseAuthManager.currentUserId ?: return

        FirebaseDbManager.getWeightHistory(userId,
            onSuccess = { entries ->
                weightHistory = entries
                setupWeightChart(entries)
                updateWeightProgress(entries)
            },
            onError = { error ->
                Toast.makeText(context, "Failed to load weight history", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateUI(profile: UserProfile) {
        // Name and initials
        val name = profile.fullName.ifEmpty { "User" }
        tvUserName.text = name
        tvInitials.text = getInitials(name)

        // Age calculation
        val age = calculateAge(profile.dateOfBirth)
        val gender = formatGender(profile.gender)
        tvUserBasicInfo.text = if (age > 0) "$age years old  |  $gender" else gender

        // Weight stats
        tvCurrentWeight.text = String.format("%.1f kg", profile.currentWeightKg)
        tvTargetWeight.text = String.format("%.1f kg", profile.targetWeightKg)
        tvHeight.text = "${profile.heightCm} cm"

        // Activity and experience
        tvActivityLevel.text = formatActivityLevel(profile.activityLevel)
        tvExperience.text = formatExperience(profile.workoutExperience)
        tvWorkoutDays.text = "${profile.workoutDaysPerWeek} days"
        tvWorkoutDuration.text = "${profile.workoutDurationMinutes} min"
        tvSleepHours.text = "${profile.sleepHoursPerNight} hours"

        // BMI calculation
        updateBMI(profile)

        // Goals chips
        setupGoalsChips(profile.fitnessGoals)

        // Dietary preferences chips
        setupDietaryChips(profile.dietaryPreferences)
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}".uppercase()
            parts.isNotEmpty() && parts[0].isNotEmpty() -> parts[0].take(2).uppercase()
            else -> "?"
        }
    }

    private fun calculateAge(dateOfBirth: String): Int {
        if (dateOfBirth.isEmpty()) return 0

        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val birthDate = format.parse(dateOfBirth) ?: return 0
            val birthCal = Calendar.getInstance().apply { time = birthDate }
            val today = Calendar.getInstance()

            var age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        } catch (e: Exception) {
            0
        }
    }

    private fun formatGender(gender: String): String {
        return when (gender) {
            "male" -> "Male"
            "female" -> "Female"
            "other" -> "Other"
            else -> "Not specified"
        }
    }

    private fun formatActivityLevel(level: String): String {
        return when (level) {
            "sedentary" -> "Sedentary"
            "lightly_active" -> "Lightly Active"
            "moderately_active" -> "Moderately Active"
            "very_active" -> "Very Active"
            "extra_active" -> "Extra Active"
            else -> "Not set"
        }
    }

    private fun formatExperience(experience: String): String {
        return when (experience) {
            "beginner" -> "Beginner"
            "intermediate" -> "Intermediate"
            "advanced" -> "Advanced"
            else -> "Not set"
        }
    }

    private fun updateBMI(profile: UserProfile) {
        if (profile.heightCm > 0 && profile.currentWeightKg > 0) {
            val heightM = profile.heightCm / 100f
            val bmi = profile.currentWeightKg / (heightM * heightM)
            val category = when {
                bmi < 18.5 -> "Underweight"
                bmi < 25 -> "Normal"
                bmi < 30 -> "Overweight"
                else -> "Obese"
            }

            val color = when {
                bmi < 18.5 -> R.color.warning
                bmi < 25 -> R.color.success
                bmi < 30 -> R.color.warning
                else -> R.color.error
            }

            tvBmi.text = String.format("%.1f (%s)", bmi, category)
            tvBmi.setTextColor(ContextCompat.getColor(requireContext(), color))
        }
    }

    private fun setupGoalsChips(goals: List<String>) {
        chipGroupGoals.removeAllViews()

        val goalLabels = mapOf(
            "lose_fat" to "Lose Fat",
            "build_muscle" to "Build Muscle",
            "maintain_weight" to "Maintain Weight",
            "improve_endurance" to "Improve Endurance",
            "increase_flexibility" to "Increase Flexibility",
            "general_health" to "General Health"
        )

        goals.forEach { goal ->
            val chip = Chip(requireContext()).apply {
                text = goalLabels[goal] ?: goal
                isClickable = false
                setChipBackgroundColorResource(R.color.primaryLight)
                setTextColor(ContextCompat.getColor(context, R.color.primaryDark))
            }
            chipGroupGoals.addView(chip)
        }

        if (goals.isEmpty()) {
            val chip = Chip(requireContext()).apply {
                text = "No goals set"
                isClickable = false
                setChipBackgroundColorResource(R.color.background)
                setTextColor(ContextCompat.getColor(context, R.color.textSecondary))
            }
            chipGroupGoals.addView(chip)
        }
    }

    private fun setupDietaryChips(preferences: List<String>) {
        chipGroupDietary.removeAllViews()

        val prefLabels = mapOf(
            "no_restrictions" to "No Restrictions",
            "vegetarian" to "Vegetarian",
            "vegan" to "Vegan",
            "pescatarian" to "Pescatarian",
            "keto" to "Keto",
            "gluten_free" to "Gluten Free",
            "dairy_free" to "Dairy Free",
            "halal" to "Halal",
            "kosher" to "Kosher"
        )

        preferences.forEach { pref ->
            val chip = Chip(requireContext()).apply {
                text = prefLabels[pref] ?: pref
                isClickable = false
                setChipBackgroundColorResource(R.color.mealBreakfast)
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            chipGroupDietary.addView(chip)
        }

        if (preferences.isEmpty()) {
            val chip = Chip(requireContext()).apply {
                text = "No preferences set"
                isClickable = false
                setChipBackgroundColorResource(R.color.background)
                setTextColor(ContextCompat.getColor(context, R.color.textSecondary))
            }
            chipGroupDietary.addView(chip)
        }
    }

    private fun setupWeightChart(entries: List<WeightEntry>) {
        if (entries.isEmpty()) {
            weightChart.setNoDataText("No weight data yet")
            weightChart.invalidate()
            return
        }

        val context = requireContext()
        val primaryColor = ContextCompat.getColor(context, R.color.primaryColor)
        val primaryLightColor = ContextCompat.getColor(context, R.color.primaryLight)
        val accentColor = ContextCompat.getColor(context, R.color.accentColor)

        // Create chart entries
        val chartEntries = entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.weightKg)
        }

        // Create dataset
        val dataSet = LineDataSet(chartEntries, "Weight").apply {
            color = primaryColor
            lineWidth = 2.5f
            setDrawCircles(true)
            circleRadius = 4f
            setCircleColor(primaryColor)
            circleHoleColor = Color.WHITE
            circleHoleRadius = 2f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = primaryLightColor
            fillAlpha = 50
        }

        // Add target weight line if available
        currentProfile?.let { profile ->
            if (profile.targetWeightKg > 0) {
                val targetLine = LimitLine(profile.targetWeightKg, "Target").apply {
                    lineWidth = 2f
                    lineColor = accentColor
                    enableDashedLine(10f, 10f, 0f)
                    labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                    textSize = 10f
                    textColor = accentColor
                }
                weightChart.axisLeft.addLimitLine(targetLine)
            }
        }

        // Configure chart appearance
        weightChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)

            // X-axis configuration
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = ContextCompat.getColor(context, R.color.textSecondary)
                textSize = 10f

                // Custom formatter for dates
                valueFormatter = object : ValueFormatter() {
                    private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index >= 0 && index < entries.size) {
                            try {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val date = inputFormat.parse(entries[index].date)
                                date?.let { dateFormat.format(it) } ?: ""
                            } catch (e: Exception) {
                                ""
                            }
                        } else ""
                    }
                }

                // Show fewer labels
                labelCount = 5
            }

            // Y-axis configuration
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(context, R.color.background)
                textColor = ContextCompat.getColor(context, R.color.textSecondary)
                textSize = 10f

                // Add some padding to the range
                val minWeight = entries.minOfOrNull { it.weightKg } ?: 0f
                val maxWeight = entries.maxOfOrNull { it.weightKg } ?: 100f
                val targetWeight = currentProfile?.targetWeightKg ?: 0f

                val effectiveMin = minOf(minWeight, targetWeight) - 2f
                val effectiveMax = maxOf(maxWeight, targetWeight) + 2f

                axisMinimum = effectiveMin
                axisMaximum = effectiveMax
            }

            axisRight.isEnabled = false

            // Animate
            animateX(800)
            invalidate()
        }
    }

    private fun updateWeightProgress(entries: List<WeightEntry>) {
        if (entries.size < 2) {
            tvWeightChange.text = "Start tracking!"
            tvWeightChange.setTextColor(ContextCompat.getColor(requireContext(), R.color.textSecondary))
            tvProgressSubtitle.text = "Add your weight to see progress"
            return
        }

        val firstWeight = entries.first().weightKg
        val lastWeight = entries.last().weightKg
        val change = lastWeight - firstWeight

        val changeText = if (change >= 0) {
            String.format("+%.1f kg", change)
        } else {
            String.format("%.1f kg", change)
        }

        tvWeightChange.text = changeText

        // Color based on goal (assuming weight loss goal for now)
        val color = when {
            change < 0 -> R.color.success  // Lost weight - good
            change > 0.5 -> R.color.error  // Gained weight - concerning
            else -> R.color.warning        // Maintained
        }
        tvWeightChange.setTextColor(ContextCompat.getColor(requireContext(), color))

        val days = entries.size
        tvProgressSubtitle.text = "Last $days days"
    }

    private fun showUpdateWeightDialog() {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_update_weight, null)

        val etWeight = dialogView.findViewById<TextInputEditText>(R.id.etWeight)
        val etNote = dialogView.findViewById<TextInputEditText>(R.id.etNote)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        // Pre-fill with current weight
        currentProfile?.let {
            etWeight.setText(String.format("%.1f", it.currentWeightKg))
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val weightText = etWeight.text.toString()
            val note = etNote.text.toString()

            if (weightText.isEmpty()) {
                etWeight.error = "Please enter weight"
                return@setOnClickListener
            }

            val weight = weightText.toFloatOrNull()
            if (weight == null || weight <= 0 || weight > 500) {
                etWeight.error = "Please enter a valid weight"
                return@setOnClickListener
            }

            saveWeight(weight, note)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveWeight(weight: Float, note: String) {
        val userId = FirebaseAuthManager.currentUserId ?: return

        FirebaseDbManager.addWeightEntry(
            userId = userId,
            weightKg = weight,
            note = note,
            onSuccess = {
                Toast.makeText(context, "Weight updated!", Toast.LENGTH_SHORT).show()
                // Refresh data
                currentProfile = currentProfile?.copy(currentWeightKg = weight)
                currentProfile?.let {
                    tvCurrentWeight.text = String.format("%.1f kg", weight)
                    updateBMI(it)
                }
                loadWeightHistory()
            },
            onError = { error ->
                Toast.makeText(context, "Failed to save: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        FirebaseAuthManager.signOut()
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}
