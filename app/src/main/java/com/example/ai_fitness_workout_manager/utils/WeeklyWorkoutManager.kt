package com.example.ai_fitness_workout_manager.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object WeeklyWorkoutManager {

    private const val PREFS_NAME = "weekly_workouts"
    private const val KEY_LAST_UPDATE_WEEK = "last_update_week"
    private const val KEY_WORKOUT_PLAN = "workout_plan"

    private val weekFormat = SimpleDateFormat("yyyy-'W'ww", Locale.getDefault())

    /**
     * Get current week identifier (e.g., "2026-W02")
     */
    private fun getCurrentWeek(): String {
        return weekFormat.format(Calendar.getInstance().time)
    }

    /**
     * Check if we have a workout plan for this week
     */
    fun hasWorkoutPlanForThisWeek(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentWeek = getCurrentWeek()
        val lastUpdateWeek = prefs.getString(KEY_LAST_UPDATE_WEEK, "")
        val workoutPlan = prefs.getString(KEY_WORKOUT_PLAN, "")

        return lastUpdateWeek == currentWeek && !workoutPlan.isNullOrEmpty()
    }

    /**
     * Get the saved workout plan for this week (if exists)
     */
    fun getWorkoutPlan(context: Context): String? {
        if (!hasWorkoutPlanForThisWeek(context)) {
            return null
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_WORKOUT_PLAN, null)
    }

    /**
     * Save a new workout plan for this week
     */
    fun saveWorkoutPlan(context: Context, workoutPlan: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentWeek = getCurrentWeek()

        prefs.edit().apply {
            putString(KEY_LAST_UPDATE_WEEK, currentWeek)
            putString(KEY_WORKOUT_PLAN, workoutPlan)
            apply()
        }
    }

    /**
     * Clear the saved workout plan (for manual refresh)
     */
    fun clearWorkoutPlan(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    /**
     * Get a formatted week title for display (e.g., "Week of Jan 13 - Jan 19")
     */
    fun getWeekTitle(): String {
        val calendar = Calendar.getInstance()

        // Set to Monday of current week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val weekStart = SimpleDateFormat("MMM d", Locale.getDefault()).format(calendar.time)

        // Set to Sunday of current week
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val weekEnd = SimpleDateFormat("MMM d", Locale.getDefault()).format(calendar.time)

        return "Week of $weekStart - $weekEnd"
    }

    /**
     * Get current date in readable format
     */
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }
}
