package com.example.ai_fitness_workout_manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.model.MealEntry

sealed class MealListItem {
    data class SectionHeader(
        val mealType: String,
        val title: String,
        val subtitle: String = "", // For showing meal name in snacks
        val totalCalories: Int,
        val iconRes: Int
    ) : MealListItem()

    data class MealItem(val meal: MealEntry) : MealListItem()
}

class GroupedMealAdapter(
    private val onMealClicked: (MealEntry) -> Unit = {},
    private val onAddMealClicked: (String) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<MealListItem> = emptyList()

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_MEAL = 1

        // Define meal type order
        val MEAL_TYPE_ORDER = listOf(
            MealEntry.TYPE_BREAKFAST,
            MealEntry.TYPE_LUNCH,
            MealEntry.TYPE_DINNER,
            MealEntry.TYPE_SNACK
        )
    }

    fun updateMeals(meals: List<MealEntry>) {
        items = buildGroupedList(meals)
        notifyDataSetChanged()
    }

    private fun buildGroupedList(meals: List<MealEntry>): List<MealListItem> {
        val result = mutableListOf<MealListItem>()

        // Group meals by type
        val groupedMeals = meals.groupBy { it.mealType }

        // Process each meal type in order
        for (mealType in MEAL_TYPE_ORDER) {
            val mealsOfType = groupedMeals[mealType] ?: emptyList()

            if (mealType == MealEntry.TYPE_SNACK) {
                // Handle snacks - number them if multiple and show meal name
                mealsOfType.forEachIndexed { index, meal ->
                    val snackTitle = if (mealsOfType.size > 1) {
                        "Snack ${index + 1}: ${meal.name}"
                    } else {
                        "Snack: ${meal.name}"
                    }

                    result.add(
                        MealListItem.SectionHeader(
                            mealType = "${MealEntry.TYPE_SNACK}_$index",
                            title = snackTitle,
                            subtitle = meal.time,
                            totalCalories = meal.calories,
                            iconRes = getIconForMealType(MealEntry.TYPE_SNACK)
                        )
                    )
                    // Add the meal item for macros display
                    result.add(MealListItem.MealItem(meal))
                }
            } else if (mealsOfType.isNotEmpty()) {
                // Regular meal types (breakfast, lunch, dinner)
                val totalCalories = mealsOfType.sumOf { it.calories }

                result.add(
                    MealListItem.SectionHeader(
                        mealType = mealType,
                        title = getTitleForMealType(mealType),
                        totalCalories = totalCalories,
                        iconRes = getIconForMealType(mealType)
                    )
                )

                mealsOfType.forEach { meal ->
                    result.add(MealListItem.MealItem(meal))
                }
            }
        }

        return result
    }

    private fun getTitleForMealType(mealType: String): String {
        return when (mealType) {
            MealEntry.TYPE_BREAKFAST -> "Breakfast"
            MealEntry.TYPE_LUNCH -> "Lunch"
            MealEntry.TYPE_DINNER -> "Dinner"
            MealEntry.TYPE_SNACK -> "Snack"
            else -> mealType.replaceFirstChar { it.uppercase() }
        }
    }

    private fun getIconForMealType(mealType: String): Int {
        return when (mealType) {
            MealEntry.TYPE_BREAKFAST -> R.drawable.ic_meal_breakfast
            MealEntry.TYPE_LUNCH -> R.drawable.ic_meal_lunch
            MealEntry.TYPE_DINNER -> R.drawable.ic_meal_dinner
            MealEntry.TYPE_SNACK -> R.drawable.ic_meal_snack
            else -> R.drawable.ic_nav_meals
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MealListItem.SectionHeader -> VIEW_TYPE_HEADER
            is MealListItem.MealItem -> VIEW_TYPE_MEAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_meal_section_header, parent, false)
                SectionHeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_meal_grouped, parent, false)
                MealViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is MealListItem.SectionHeader -> (holder as SectionHeaderViewHolder).bind(item)
            is MealListItem.MealItem -> (holder as MealViewHolder).bind(item.meal)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class SectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivSectionIcon: ImageView = itemView.findViewById(R.id.ivSectionIcon)
        private val tvSectionTitle: TextView = itemView.findViewById(R.id.tvSectionTitle)
        private val tvSectionCalories: TextView = itemView.findViewById(R.id.tvSectionCalories)

        fun bind(header: MealListItem.SectionHeader) {
            tvSectionTitle.text = header.title
            tvSectionCalories.text = "${header.totalCalories} kcal"

            // Try to set custom icon, fallback to default if not found
            try {
                ivSectionIcon.setImageResource(header.iconRes)
            } catch (e: Exception) {
                ivSectionIcon.setImageResource(R.drawable.ic_nav_meals)
            }

            // Set icon tint based on meal type
            val tintColor = when {
                header.mealType == MealEntry.TYPE_BREAKFAST -> R.color.mealBreakfast
                header.mealType == MealEntry.TYPE_LUNCH -> R.color.mealLunch
                header.mealType == MealEntry.TYPE_DINNER -> R.color.mealDinner
                header.mealType.startsWith(MealEntry.TYPE_SNACK) -> R.color.mealSnack
                else -> R.color.primaryColor
            }
            ivSectionIcon.setColorFilter(ContextCompat.getColor(itemView.context, tintColor))
        }
    }

    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMealName: TextView = itemView.findViewById(R.id.tvMealName)
        private val tvMealTime: TextView = itemView.findViewById(R.id.tvMealTime)
        private val tvMealMacros: TextView = itemView.findViewById(R.id.tvMealMacros)

        fun bind(meal: MealEntry) {
            val context = itemView.context

            tvMealName.text = meal.name
            tvMealTime.text = meal.time

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
