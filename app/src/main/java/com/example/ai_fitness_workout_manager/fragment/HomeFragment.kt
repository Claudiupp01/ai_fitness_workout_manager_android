package com.example.ai_fitness_workout_manager.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.adapter.DayAdapter
import com.example.ai_fitness_workout_manager.adapter.GroupedMealAdapter
import com.example.ai_fitness_workout_manager.firebase.FirebaseAuthManager
import com.example.ai_fitness_workout_manager.firebase.FirebaseDbManager
import com.example.ai_fitness_workout_manager.model.DailyNutrition
import com.example.ai_fitness_workout_manager.model.DayItem
import com.example.ai_fitness_workout_manager.model.MealEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvMonthYear: TextView
    private lateinit var rvDays: RecyclerView
    private lateinit var btnPrevWeek: ImageView
    private lateinit var btnNextWeek: ImageView
    private lateinit var rvMeals: RecyclerView
    private lateinit var tvEmptyMeals: TextView
    private lateinit var btnAddMeal: Button

    // Nutrition progress views
    private lateinit var progressCalories: ProgressBar
    private lateinit var progressProtein: ProgressBar
    private lateinit var progressCarbs: ProgressBar
    private lateinit var progressFat: ProgressBar
    private lateinit var progressFiber: ProgressBar
    private lateinit var tvCaloriesValue: TextView
    private lateinit var tvProteinValue: TextView
    private lateinit var tvCarbsValue: TextView
    private lateinit var tvFatValue: TextView
    private lateinit var tvFiberValue: TextView

    private lateinit var dayAdapter: DayAdapter
    private lateinit var groupedMealAdapter: GroupedMealAdapter

    private var selectedDate: Date = Date()
    private var weekOffset: Int = 0

    companion object {
        private const val PREFS_NAME = "fitness_app_prefs"
        private const val KEY_SAMPLE_DATA_POPULATED = "sample_data_populated"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupDaySelector()
        setupMealsList()
        loadUserName()
        checkAndPopulateSampleData()
    }

    private fun initViews(view: View) {
        tvWelcome = view.findViewById(R.id.tvWelcome)
        tvMonthYear = view.findViewById(R.id.tvMonthYear)
        rvDays = view.findViewById(R.id.rvDays)
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek)
        btnNextWeek = view.findViewById(R.id.btnNextWeek)
        rvMeals = view.findViewById(R.id.rvMeals)
        tvEmptyMeals = view.findViewById(R.id.tvEmptyMeals)
        btnAddMeal = view.findViewById(R.id.btnAddMeal)

        // Nutrition progress views
        progressCalories = view.findViewById(R.id.progressCalories)
        progressProtein = view.findViewById(R.id.progressProtein)
        progressCarbs = view.findViewById(R.id.progressCarbs)
        progressFat = view.findViewById(R.id.progressFat)
        progressFiber = view.findViewById(R.id.progressFiber)
        tvCaloriesValue = view.findViewById(R.id.tvCaloriesValue)
        tvProteinValue = view.findViewById(R.id.tvProteinValue)
        tvCarbsValue = view.findViewById(R.id.tvCarbsValue)
        tvFatValue = view.findViewById(R.id.tvFatValue)
        tvFiberValue = view.findViewById(R.id.tvFiberValue)

        btnAddMeal.setOnClickListener {
            Toast.makeText(requireContext(), "Add meal feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDaySelector() {
        val days = DayItem.generateWeekDays(selectedDate, weekOffset)

        dayAdapter = DayAdapter(days) { selectedDay ->
            selectedDate = selectedDay.date
            updateDaySelector()
            loadMealsForDate(selectedDay.date)
        }

        rvDays.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dayAdapter
        }

        // Initialize month/year label
        updateMonthYearLabel(days)

        btnPrevWeek.setOnClickListener {
            weekOffset--
            updateDaySelector()
        }

        btnNextWeek.setOnClickListener {
            weekOffset++
            updateDaySelector()
        }
    }

    private fun updateDaySelector() {
        val days = DayItem.generateWeekDays(selectedDate, weekOffset)
        dayAdapter.updateDays(days)
        updateMonthYearLabel(days)
    }

    private fun updateMonthYearLabel(days: List<DayItem>) {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

        // Check if week spans two months
        val firstDay = days.first()
        val lastDay = days.last()
        val firstMonth = SimpleDateFormat("MMMM", Locale.getDefault()).format(firstDay.date)
        val lastMonth = SimpleDateFormat("MMMM", Locale.getDefault()).format(lastDay.date)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(lastDay.date)

        tvMonthYear.text = if (firstMonth != lastMonth) {
            // Week spans two months (e.g., "December - January 2026")
            val firstYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(firstDay.date)
            if (firstYear != year) {
                "$firstMonth $firstYear - $lastMonth $year"
            } else {
                "$firstMonth - $lastMonth $year"
            }
        } else {
            dateFormat.format(firstDay.date)
        }
    }

    private fun setupMealsList() {
        groupedMealAdapter = GroupedMealAdapter(
            onMealClicked = { meal ->
                Toast.makeText(requireContext(), "Clicked: ${meal.name}", Toast.LENGTH_SHORT).show()
            },
            onAddMealClicked = { mealType ->
                Toast.makeText(requireContext(), "Add $mealType coming soon!", Toast.LENGTH_SHORT).show()
            }
        )

        rvMeals.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupedMealAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun checkAndPopulateSampleData() {
        val userId = FirebaseAuthManager.currentUserId ?: return

        // Check if we've already populated sample data for this user
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = "${KEY_SAMPLE_DATA_POPULATED}_$userId"

        if (!prefs.getBoolean(key, false)) {
            // Populate sample data
            FirebaseDbManager.populateSampleMeals(
                userId = userId,
                onSuccess = {
                    // Mark as populated
                    prefs.edit().putBoolean(key, true).apply()
                    // Load meals for today
                    loadMealsForDate(selectedDate)
                },
                onError = { error ->
                    // Still try to load existing meals
                    loadMealsForDate(selectedDate)
                }
            )
        } else {
            // Sample data already exists, just load meals
            loadMealsForDate(selectedDate)
        }
    }

    private fun loadUserName() {
        val userId = FirebaseAuthManager.currentUserId
        if (userId != null) {
            FirebaseDbManager.getUserProfile(userId,
                onSuccess = { profile ->
                    val name = profile?.fullName?.ifEmpty { "User" } ?: "User"
                    val firstName = name.split(" ").firstOrNull() ?: name
                    tvWelcome.text = getString(R.string.welcome_user, firstName)
                },
                onError = {
                    tvWelcome.text = getString(R.string.welcome_default)
                }
            )
        } else {
            tvWelcome.text = getString(R.string.welcome_default)
        }
    }

    private fun loadMealsForDate(date: Date) {
        val userId = FirebaseAuthManager.currentUserId
        if (userId == null) {
            showEmptyState()
            return
        }

        val dateStr = FirebaseDbManager.formatDateForDb(date)

        FirebaseDbManager.getMealsForDate(
            userId = userId,
            date = dateStr,
            onSuccess = { meals ->
                if (meals.isEmpty()) {
                    showEmptyState()
                    updateNutritionUI(DailyNutrition())
                } else {
                    showMeals(meals)
                    updateNutritionUI(DailyNutrition(meals = meals))
                }
            },
            onError = { error ->
                showEmptyState()
                updateNutritionUI(DailyNutrition())
            }
        )
    }

    private fun showEmptyState() {
        rvMeals.visibility = View.GONE
        tvEmptyMeals.visibility = View.VISIBLE
    }

    private fun showMeals(meals: List<MealEntry>) {
        rvMeals.visibility = View.VISIBLE
        tvEmptyMeals.visibility = View.GONE
        groupedMealAdapter.updateMeals(meals)
    }

    private fun updateNutritionUI(nutrition: DailyNutrition) {
        // Update progress (convert to percentage 0-100)
        progressCalories.progress = (nutrition.caloriesProgress * 100).toInt()
        progressProtein.progress = (nutrition.proteinProgress * 100).toInt()
        progressCarbs.progress = (nutrition.carbsProgress * 100).toInt()
        progressFat.progress = (nutrition.fatProgress * 100).toInt()
        progressFiber.progress = (nutrition.fiberProgress * 100).toInt()

        // Update value texts with current/goal format
        tvCaloriesValue.text = "${nutrition.totalCalories}/${nutrition.goalCalories}"
        tvProteinValue.text = "${nutrition.totalProtein.toInt()}/${nutrition.goalProtein.toInt()}g"
        tvCarbsValue.text = "${nutrition.totalCarbs.toInt()}/${nutrition.goalCarbs.toInt()}g"
        tvFatValue.text = "${nutrition.totalFat.toInt()}/${nutrition.goalFat.toInt()}g"
        tvFiberValue.text = "${nutrition.totalFiber.toInt()}/${nutrition.goalFiber.toInt()}g"
    }
}
