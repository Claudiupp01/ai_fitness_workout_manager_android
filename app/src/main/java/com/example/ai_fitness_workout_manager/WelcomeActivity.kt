package com.example.ai_fitness_workout_manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import android.widget.Button
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class WelcomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnNext: Button
    private lateinit var btnSkip: TextView
    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if this is first launch
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)

        if (!isFirstLaunch) {
            // Not first launch, go directly to LoginActivity
            navigateToLogin()
            return
        }

        setContentView(R.layout.activity_welcome)

        initViews()
        setupViewPager()
        setupClickListeners()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPagerOnboarding)
        tabLayout = findViewById(R.id.tabLayoutIndicator)
        btnNext = findViewById(R.id.btnNext)
        btnSkip = findViewById(R.id.btnSkip)
    }

    private fun setupViewPager() {
        val onboardingItems = listOf(
            OnboardingItem(
                title = "Track Your Fitness Journey",
                description = "Log your workouts, monitor progress, and achieve your fitness goals with personalized tracking.",
                imageResource = android.R.drawable.ic_menu_compass
            ),
            OnboardingItem(
                title = "Smart Meal Planning",
                description = "Get AI-powered meal recommendations, track your nutrition, and register meals with your camera.",
                imageResource = android.R.drawable.ic_menu_today
            ),
            OnboardingItem(
                title = "AI Fitness Assistant",
                description = "Your personal AI coach provides workout recommendations, answers questions, and keeps you motivated.",
                imageResource = android.R.drawable.ic_menu_info_details
            ),
            OnboardingItem(
                title = "Monitor Your Progress",
                description = "Track your weight, calories burned, steps, and see your daily statistics all in one place.",
                imageResource = android.R.drawable.ic_menu_agenda
            ),
            OnboardingItem(
                title = "Ready to Get Started?",
                description = "Join thousands of users transforming their health and fitness. Let's begin your journey!",
                imageResource = android.R.drawable.ic_menu_send
            )
        )

        onboardingAdapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = onboardingAdapter

        // Link TabLayout with ViewPager2 for indicators
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        // Page change listener to update button text
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == onboardingItems.size - 1) {
                    btnNext.text = "Get Started"
                } else {
                    btnNext.text = "Next"
                }
            }
        })
    }

    private fun setupClickListeners() {
        btnNext.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < onboardingAdapter.itemCount - 1) {
                viewPager.currentItem = currentItem + 1
            } else {
                finishOnboarding()
            }
        }

        btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        // Mark that user has seen the welcome screen
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isFirstLaunch", false)
            apply()
        }

        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}