package com.example.ai_fitness_workout_manager.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.adapter.RecipeAdapter
import com.example.ai_fitness_workout_manager.database.RecipeDatabase
import com.example.ai_fitness_workout_manager.model.MealRecipe
import com.example.ai_fitness_workout_manager.utils.DailyRecipeManager

class MealsActivity : AppCompatActivity() {

    private lateinit var rvBreakfast: RecyclerView
    private lateinit var rvLunch: RecyclerView
    private lateinit var rvDinner: RecyclerView
    private lateinit var btnBack: ImageView
    private lateinit var btnCamera: ImageView

    private lateinit var breakfastAdapter: RecipeAdapter
    private lateinit var lunchAdapter: RecipeAdapter
    private lateinit var dinnerAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meals)

        initializeViews()
        setupAdapters()
        loadRandomRecipes()
        setupClickListeners()
    }

    private fun initializeViews() {
        rvBreakfast = findViewById(R.id.rvBreakfast)
        rvLunch = findViewById(R.id.rvLunch)
        rvDinner = findViewById(R.id.rvDinner)
        btnBack = findViewById(R.id.btnBack)
        btnCamera = findViewById(R.id.btnCamera)
    }

    private fun setupAdapters() {
        // Breakfast Adapter
        breakfastAdapter = RecipeAdapter { recipe ->
            showRecipeDetails(recipe)
        }
        rvBreakfast.apply {
            layoutManager = LinearLayoutManager(this@MealsActivity)
            adapter = breakfastAdapter
            isNestedScrollingEnabled = false
        }

        // Lunch Adapter
        lunchAdapter = RecipeAdapter { recipe ->
            showRecipeDetails(recipe)
        }
        rvLunch.apply {
            layoutManager = LinearLayoutManager(this@MealsActivity)
            adapter = lunchAdapter
            isNestedScrollingEnabled = false
        }

        // Dinner Adapter
        dinnerAdapter = RecipeAdapter { recipe ->
            showRecipeDetails(recipe)
        }
        rvDinner.apply {
            layoutManager = LinearLayoutManager(this@MealsActivity)
            adapter = dinnerAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun loadRandomRecipes() {
        // Get today's recipes (cached per day)
        val dailyRecipes = DailyRecipeManager.getTodaysRecipes(this)

        breakfastAdapter.submitList(dailyRecipes.breakfast)
        lunchAdapter.submitList(dailyRecipes.lunch)
        dinnerAdapter.submitList(dailyRecipes.dinner)

        // Force RecyclerViews to measure all items after data is set
        rvBreakfast.post { setRecyclerViewHeight(rvBreakfast) }
        rvLunch.post { setRecyclerViewHeight(rvLunch) }
        rvDinner.post { setRecyclerViewHeight(rvDinner) }
    }

    /**
     * Fix for RecyclerView with wrap_content inside ScrollView
     * Forces the RecyclerView to calculate its height based on all children
     */
    private fun setRecyclerViewHeight(recyclerView: RecyclerView) {
        val adapter = recyclerView.adapter ?: return

        // Get available width accounting for RecyclerView padding
        val availableWidth = recyclerView.width - recyclerView.paddingLeft - recyclerView.paddingRight

        // If width is 0, the view hasn't been laid out yet, try again later
        if (availableWidth <= 0) {
            recyclerView.post { setRecyclerViewHeight(recyclerView) }
            return
        }

        var totalHeight = 0
        for (i in 0 until adapter.itemCount) {
            val viewHolder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
            adapter.onBindViewHolder(viewHolder, i)

            viewHolder.itemView.measure(
                View.MeasureSpec.makeMeasureSpec(availableWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            totalHeight += viewHolder.itemView.measuredHeight

            // Add margins (cards have marginVertical="8dp" which is 16dp total per item)
            val layoutParams = viewHolder.itemView.layoutParams as? ViewGroup.MarginLayoutParams
            if (layoutParams != null) {
                totalHeight += layoutParams.topMargin + layoutParams.bottomMargin
            }
        }

        val params = recyclerView.layoutParams
        params.height = totalHeight
        recyclerView.layoutParams = params
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCamera.setOnClickListener {
            // Placeholder for future AI ingredient recognition feature
            Toast.makeText(
                this,
                "AI Ingredient Recognition coming soon!\nThis will use your camera to identify ingredients and suggest recipes.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showRecipeDetails(recipe: MealRecipe) {
        val intent = Intent(this, RecipeDetailsActivity::class.java)
        intent.putExtra("RECIPE_ID", recipe.id)
        startActivity(intent)
    }
}
