package com.example.ai_fitness_workout_manager.model

import java.util.Calendar
import java.util.Date

data class DayItem(
    val date: Date,
    val dayOfWeek: String,
    val dayOfMonth: Int,
    val isToday: Boolean = false,
    val isSelected: Boolean = false
) {
    companion object {
        fun generateWeekDays(selectedDate: Date, weekOffset: Int = 0): List<DayItem> {
            val today = Calendar.getInstance()
            val calendar = Calendar.getInstance()

            // Start from TODAY (not selectedDate) and move to start of week (Monday)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
            calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday)

            // Apply week offset from today's week
            calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

            val selectedCal = Calendar.getInstance().apply { time = selectedDate }

            val dayNames = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

            return (0..6).map { index ->
                val currentDate = calendar.time
                val isToday = isSameDay(calendar, today)
                val isSelected = isSameDay(calendar, selectedCal)

                val item = DayItem(
                    date = currentDate,
                    dayOfWeek = dayNames[index],
                    dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                    isToday = isToday,
                    isSelected = isSelected
                )

                calendar.add(Calendar.DAY_OF_MONTH, 1)
                item
            }
        }

        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                   cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }
    }
}
