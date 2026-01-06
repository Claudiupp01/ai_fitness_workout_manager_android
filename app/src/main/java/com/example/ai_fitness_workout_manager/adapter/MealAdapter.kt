package com.example.ai_fitness_workout_manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.model.MealEntry

class MealAdapter(
    private var meals: List<MealEntry>,
    private val onMealClicked: (MealEntry) -> Unit = {}
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    fun updateMeals(newMeals: List<MealEntry>) {
        meals = newMeals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int = meals.size

    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivMealIcon: ImageView = itemView.findViewById(R.id.ivMealIcon)
        private val tvMealType: TextView = itemView.findViewById(R.id.tvMealType)
        private val tvMealTime: TextView = itemView.findViewById(R.id.tvMealTime)
        private val tvMealName: TextView = itemView.findViewById(R.id.tvMealName)
        private val tvMealMacros: TextView = itemView.findViewById(R.id.tvMealMacros)

        fun bind(meal: MealEntry) {
            val context = itemView.context

            // Set meal type text
            tvMealType.text = when (meal.mealType) {
                MealEntry.TYPE_BREAKFAST -> context.getString(R.string.breakfast)
                MealEntry.TYPE_LUNCH -> context.getString(R.string.lunch)
                MealEntry.TYPE_DINNER -> context.getString(R.string.dinner)
                MealEntry.TYPE_SNACK -> context.getString(R.string.snack)
                else -> meal.mealType.replaceFirstChar { it.uppercase() }
            }

            // Set meal icon based on type
            val iconRes = when (meal.mealType) {
                MealEntry.TYPE_BREAKFAST -> android.R.drawable.ic_menu_day
                MealEntry.TYPE_LUNCH -> android.R.drawable.ic_menu_today
                MealEntry.TYPE_DINNER -> android.R.drawable.ic_menu_myplaces
                MealEntry.TYPE_SNACK -> android.R.drawable.ic_menu_add
                else -> R.drawable.ic_nav_meals
            }
            ivMealIcon.setImageResource(iconRes)

            tvMealTime.text = meal.time
            tvMealName.text = meal.name

            // Format macros string
            tvMealMacros.text = context.getString(
                R.string.meal_macros,
                meal.calories,
                meal.protein,
                meal.carbs,
                meal.fat
            )

            itemView.setOnClickListener {
                onMealClicked(meal)
            }
        }
    }
}
