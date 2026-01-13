package com.example.ai_fitness_workout_manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.model.MealRecipe

class RecipeAdapter(
    private val onRecipeClicked: (MealRecipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var recipes = listOf<MealRecipe>()

    fun submitList(newRecipes: List<MealRecipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount() = recipes.size

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRecipeName: TextView = itemView.findViewById(R.id.tvRecipeName)
        private val tvRecipeDescription: TextView = itemView.findViewById(R.id.tvRecipeDescription)
        private val tvDifficulty: TextView = itemView.findViewById(R.id.tvDifficulty)
        private val tvPrepTime: TextView = itemView.findViewById(R.id.tvPrepTime)
        private val tvCookTime: TextView = itemView.findViewById(R.id.tvCookTime)
        private val tvServings: TextView = itemView.findViewById(R.id.tvServings)
        private val tvCalories: TextView = itemView.findViewById(R.id.tvCalories)
        private val tvProtein: TextView = itemView.findViewById(R.id.tvProtein)
        private val tvCarbs: TextView = itemView.findViewById(R.id.tvCarbs)
        private val tvFat: TextView = itemView.findViewById(R.id.tvFat)

        fun bind(recipe: MealRecipe) {
            tvRecipeName.text = recipe.name
            tvRecipeDescription.text = recipe.description
            tvDifficulty.text = recipe.difficulty.replaceFirstChar { it.uppercase() }
            tvPrepTime.text = "${recipe.prepTime} min"
            tvCookTime.text = "${recipe.cookTime} min"
            tvServings.text = "${recipe.servings} ${if (recipe.servings == 1) "serving" else "servings"}"
            tvCalories.text = recipe.calories.toString()
            tvProtein.text = "${recipe.protein.toInt()}g"
            tvCarbs.text = "${recipe.carbs.toInt()}g"
            tvFat.text = "${recipe.fat.toInt()}g"

            // Set difficulty badge colors
            when (recipe.difficulty.lowercase()) {
                "easy" -> {
                    tvDifficulty.setBackgroundResource(R.drawable.chip_background_easy)
                    tvDifficulty.setTextColor(itemView.context.getColor(R.color.difficultyEasy))
                }
                "medium" -> {
                    tvDifficulty.setBackgroundResource(R.drawable.chip_background_medium)
                    tvDifficulty.setTextColor(itemView.context.getColor(R.color.difficultyMedium))
                }
                "hard" -> {
                    tvDifficulty.setBackgroundResource(R.drawable.chip_background_hard)
                    tvDifficulty.setTextColor(itemView.context.getColor(R.color.difficultyHard))
                }
            }

            itemView.setOnClickListener {
                onRecipeClicked(recipe)
            }
        }
    }
}
