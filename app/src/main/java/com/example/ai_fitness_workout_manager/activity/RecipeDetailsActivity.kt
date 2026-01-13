package com.example.ai_fitness_workout_manager.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.adapter.IngredientAdapter
import com.example.ai_fitness_workout_manager.database.RecipeDatabase
import com.example.ai_fitness_workout_manager.model.MealRecipe

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvRecipeName: TextView
    private lateinit var tvRecipeDescription: TextView
    private lateinit var tvPrepTime: TextView
    private lateinit var tvCookTime: TextView
    private lateinit var tvServings: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvProtein: TextView
    private lateinit var tvCarbs: TextView
    private lateinit var tvFat: TextView
    private lateinit var tvFiber: TextView
    private lateinit var rvIngredients: RecyclerView
    private lateinit var llSteps: LinearLayout

    private lateinit var ingredientAdapter: IngredientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        initializeViews()
        setupAdapters()
        loadRecipeDetails()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        tvRecipeName = findViewById(R.id.tvRecipeName)
        tvRecipeDescription = findViewById(R.id.tvRecipeDescription)
        tvPrepTime = findViewById(R.id.tvPrepTime)
        tvCookTime = findViewById(R.id.tvCookTime)
        tvServings = findViewById(R.id.tvServings)
        tvCalories = findViewById(R.id.tvCalories)
        tvProtein = findViewById(R.id.tvProtein)
        tvCarbs = findViewById(R.id.tvCarbs)
        tvFat = findViewById(R.id.tvFat)
        tvFiber = findViewById(R.id.tvFiber)
        rvIngredients = findViewById(R.id.rvIngredients)
        llSteps = findViewById(R.id.llSteps)
    }

    private fun setupAdapters() {
        ingredientAdapter = IngredientAdapter()
        rvIngredients.apply {
            layoutManager = LinearLayoutManager(this@RecipeDetailsActivity)
            adapter = ingredientAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun loadRecipeDetails() {
        val recipeId = intent.getStringExtra("RECIPE_ID")
        if (recipeId == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val recipe = RecipeDatabase.getRecipeById(recipeId)
        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        displayRecipe(recipe)
    }

    private fun displayRecipe(recipe: MealRecipe) {
        // Header
        tvRecipeName.text = recipe.name
        tvRecipeDescription.text = recipe.description

        // Info
        tvPrepTime.text = "Prep: ${recipe.prepTime} min"
        tvCookTime.text = "Cook: ${recipe.cookTime} min"
        tvServings.text = "${recipe.servings} ${if (recipe.servings == 1) "serving" else "servings"}"

        // Nutrition
        tvCalories.text = recipe.calories.toString()
        tvProtein.text = "${recipe.protein.toInt()}g"
        tvCarbs.text = "${recipe.carbs.toInt()}g"
        tvFat.text = "${recipe.fat.toInt()}g"
        tvFiber.text = "${recipe.fiber.toInt()}g"

        // Ingredients
        ingredientAdapter.submitList(recipe.ingredients)

        // Steps - add directly to LinearLayout
        populateSteps(recipe.steps)
    }

    private fun populateSteps(steps: List<String>) {
        llSteps.removeAllViews()

        val inflater = LayoutInflater.from(this)

        steps.forEachIndexed { index, step ->
            val stepView = inflater.inflate(R.layout.item_step, llSteps, false)

            // Ensure proper layout params
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            stepView.layoutParams = layoutParams

            val tvStepNumber = stepView.findViewById<TextView>(R.id.tvStepNumber)
            val tvStepDescription = stepView.findViewById<TextView>(R.id.tvStepDescription)

            tvStepNumber.text = (index + 1).toString()
            tvStepDescription.text = step

            llSteps.addView(stepView)
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }
}
