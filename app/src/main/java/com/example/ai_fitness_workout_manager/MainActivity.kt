package com.example.ai_fitness_workout_manager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ai_fitness_workout_manager.firebase.FirebaseAuthManager
import com.example.ai_fitness_workout_manager.fragment.AIAssistantFragment
import com.example.ai_fitness_workout_manager.fragment.HomeFragment
import com.example.ai_fitness_workout_manager.fragment.PlaceholderFragment
import com.example.ai_fitness_workout_manager.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    // Keep fragments to avoid recreating them
    private var homeFragment: HomeFragment? = null
    private var aiAssistantFragment: AIAssistantFragment? = null
    private var mealsFragment: PlaceholderFragment? = null
    private var workoutFragment: PlaceholderFragment? = null
    private var profileFragment: ProfileFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in
        if (!FirebaseAuthManager.isLoggedIn) {
            navigateToAuth()
            return
        }

        setContentView(R.layout.activity_main)
        initViews()
        setupBottomNavigation()

        // Set home as default selected tab
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    private fun initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showFragment(getHomeFragment())
                    true
                }
                R.id.nav_ai_assistant -> {
                    showFragment(getAIAssistantFragment())
                    true
                }
                R.id.nav_meals -> {
                    showFragment(getMealsFragment())
                    true
                }
                R.id.nav_workout -> {
                    showFragment(getWorkoutFragment())
                    true
                }
                R.id.nav_profile -> {
                    showFragment(getProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun getHomeFragment(): HomeFragment {
        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance()
        }
        return homeFragment!!
    }

    private fun getAIAssistantFragment(): AIAssistantFragment {
        if (aiAssistantFragment == null) {
            aiAssistantFragment = AIAssistantFragment.newInstance()
        }
        return aiAssistantFragment!!
    }

    private fun getMealsFragment(): PlaceholderFragment {
        if (mealsFragment == null) {
            mealsFragment = PlaceholderFragment.newInstance(
                title = getString(R.string.nav_meals),
                message = getString(R.string.meals_coming_soon),
                iconResId = R.drawable.ic_nav_meals
            )
        }
        return mealsFragment!!
    }

    private fun getWorkoutFragment(): PlaceholderFragment {
        if (workoutFragment == null) {
            workoutFragment = PlaceholderFragment.newInstance(
                title = getString(R.string.nav_workout),
                message = getString(R.string.workout_coming_soon),
                iconResId = R.drawable.ic_nav_workout
            )
        }
        return workoutFragment!!
    }

    private fun getProfileFragment(): ProfileFragment {
        if (profileFragment == null) {
            profileFragment = ProfileFragment.newInstance()
        }
        return profileFragment!!
    }

    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
