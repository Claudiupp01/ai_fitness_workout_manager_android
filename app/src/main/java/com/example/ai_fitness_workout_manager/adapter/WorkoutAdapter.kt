package com.example.ai_fitness_workout_manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.model.DailyWorkout
import com.google.android.material.chip.Chip

class WorkoutAdapter(
    private val workouts: List<DailyWorkout>,
    private val onWorkoutClick: (DailyWorkout) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_day, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.bind(workout)
    }

    override fun getItemCount(): Int = workouts.size

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDayName: TextView = itemView.findViewById(R.id.tvDayName)
        private val tvWorkoutType: TextView = itemView.findViewById(R.id.tvWorkoutType)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        private val tvTargetMuscles: TextView = itemView.findViewById(R.id.tvTargetMuscles)
        private val tvExerciseCount: TextView = itemView.findViewById(R.id.tvExerciseCount)
        private val chipDifficulty: Chip = itemView.findViewById(R.id.chipDifficulty)

        fun bind(workout: DailyWorkout) {
            tvDayName.text = workout.dayOfWeek

            // Handle rest days
            if (workout.isRestDay) {
                tvWorkoutType.text = "Rest Day"
                tvDuration.text = "Recovery"
                tvTargetMuscles.text = "Take a break and let your muscles recover"
                tvExerciseCount.text = "No exercises"
                chipDifficulty.visibility = View.GONE

                // Optional: Change card appearance for rest days
                itemView.alpha = 0.7f
            } else {
                tvWorkoutType.text = workout.workoutType
                tvDuration.text = "${workout.duration} min"
                tvTargetMuscles.text = "Target: ${workout.targetMuscles.joinToString(", ")}"
                tvExerciseCount.text = "${workout.exerciseCount} exercises"

                // Set difficulty badge
                chipDifficulty.visibility = View.VISIBLE
                chipDifficulty.text = workout.difficulty
                chipDifficulty.setChipBackgroundColorResource(getDifficultyColor(workout.difficulty))

                itemView.alpha = 1.0f
            }

            // Click listener
            itemView.setOnClickListener {
                onWorkoutClick(workout)
            }
        }

        private fun getDifficultyColor(difficulty: String): Int {
            return when (difficulty.lowercase()) {
                "easy" -> R.color.difficultyEasy
                "medium" -> R.color.difficultyMedium
                "hard" -> R.color.difficultyHard
                else -> R.color.difficultyMedium
            }
        }
    }
}
