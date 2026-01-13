package com.example.ai_fitness_workout_manager.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_fitness_workout_manager.R

class RecipeRecommendationsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnCopy: ImageView
    private lateinit var tvRecommendations: TextView

    companion object {
        const val EXTRA_RECOMMENDATIONS = "extra_recommendations"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_recommendations)

        initializeViews()
        setupClickListeners()
        displayRecommendations()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        btnCopy = findViewById(R.id.btnCopy)
        tvRecommendations = findViewById(R.id.tvRecommendations)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCopy.setOnClickListener {
            copyRecommendationsToClipboard()
        }
    }

    private fun displayRecommendations() {
        val recommendations = intent.getStringExtra(EXTRA_RECOMMENDATIONS)

        if (recommendations.isNullOrEmpty()) {
            tvRecommendations.text = "No recommendations available."
            Toast.makeText(this, "No recommendations to display", Toast.LENGTH_SHORT).show()
        } else {
            tvRecommendations.text = recommendations
        }
    }

    private fun copyRecommendationsToClipboard() {
        val recommendations = tvRecommendations.text.toString()

        if (recommendations.isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Recipe Recommendations", recommendations)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show()
        }
    }
}
