package com.example.ai_fitness_workout_manager

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_fitness_workout_manager.firebase.FirebaseAuthManager
import com.example.ai_fitness_workout_manager.firebase.FirebaseDbManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AuthActivity : AppCompatActivity() {

    private lateinit var tabLogin: TextView
    private lateinit var tabSignUp: TextView
    private lateinit var tilFullName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tvForgotPassword: TextView
    private lateinit var btnAction: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var tvBottomText: TextView
    private lateinit var tvBottomAction: TextView
    private lateinit var tvSubtitle: TextView

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if already logged in - navigate directly without showing UI
        if (FirebaseAuthManager.isLoggedIn) {
            navigateBasedOnProfile()
            return
        }

        setContentView(R.layout.activity_auth)
        initViews()
        setupClickListeners()
        updateUIForMode()
    }

    private fun navigateBasedOnProfile() {
        val userId = FirebaseAuthManager.currentUserId
        if (userId == null) {
            // Not logged in, show auth screen
            setContentView(R.layout.activity_auth)
            initViews()
            setupClickListeners()
            updateUIForMode()
            return
        }

        FirebaseDbManager.isProfileCompleted(userId) { isCompleted ->
            if (isCompleted) {
                navigateToMain()
            } else {
                navigateToProfileSetup()
            }
        }
    }

    private fun initViews() {
        tabLogin = findViewById(R.id.tabLogin)
        tabSignUp = findViewById(R.id.tabSignUp)
        tilFullName = findViewById(R.id.tilFullName)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        btnAction = findViewById(R.id.btnAction)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        tvBottomText = findViewById(R.id.tvBottomText)
        tvBottomAction = findViewById(R.id.tvBottomAction)
        tvSubtitle = findViewById(R.id.tvSubtitle)
    }

    private fun setupClickListeners() {
        tabLogin.setOnClickListener {
            if (!isLoginMode) {
                isLoginMode = true
                updateUIForMode()
            }
        }

        tabSignUp.setOnClickListener {
            if (isLoginMode) {
                isLoginMode = false
                updateUIForMode()
            }
        }

        tvBottomAction.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUIForMode()
        }

        tvForgotPassword.setOnClickListener {
            handleForgotPassword()
        }

        btnAction.setOnClickListener {
            if (isLoginMode) {
                handleLogin()
            } else {
                handleSignUp()
            }
        }
    }

    private fun updateUIForMode() {
        clearErrors()

        if (isLoginMode) {
            // Login mode
            tabLogin.setBackgroundResource(R.drawable.button_rounded_primary)
            tabLogin.setTextColor(getColor(R.color.white))
            tabSignUp.setBackgroundColor(getColor(R.color.transparent))
            tabSignUp.setTextColor(getColor(R.color.textSecondary))

            tilFullName.visibility = View.GONE
            tilConfirmPassword.visibility = View.GONE
            tvForgotPassword.visibility = View.VISIBLE

            btnAction.text = getString(R.string.login)
            tvSubtitle.text = getString(R.string.auth_login_subtitle)
            tvBottomText.text = getString(R.string.dont_have_account)
            tvBottomAction.text = getString(R.string.sign_up)
        } else {
            // Sign Up mode
            tabSignUp.setBackgroundResource(R.drawable.button_rounded_primary)
            tabSignUp.setTextColor(getColor(R.color.white))
            tabLogin.setBackgroundColor(getColor(R.color.transparent))
            tabLogin.setTextColor(getColor(R.color.textSecondary))

            tilFullName.visibility = View.VISIBLE
            tilConfirmPassword.visibility = View.VISIBLE
            tvForgotPassword.visibility = View.GONE

            btnAction.text = getString(R.string.sign_up)
            tvSubtitle.text = getString(R.string.auth_signup_subtitle)
            tvBottomText.text = getString(R.string.already_have_account)
            tvBottomAction.text = getString(R.string.login)
        }
    }

    private fun handleLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (!validateLoginInput(email, password)) return

        showLoading(true)
        clearErrors()

        FirebaseAuthManager.signIn(
            email = email,
            password = password,
            onSuccess = { user ->
                showLoading(false)
                checkProfileAndNavigate()
            },
            onError = { error ->
                showLoading(false)
                showError(error)
            }
        )
    }

    private fun handleSignUp() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (!validateSignUpInput(fullName, email, password, confirmPassword)) return

        showLoading(true)
        clearErrors()

        FirebaseAuthManager.signUp(
            email = email,
            password = password,
            onSuccess = { user ->
                // Create user profile in database
                FirebaseDbManager.createUserProfile(
                    userId = user.uid,
                    email = email,
                    onSuccess = {
                        // Update the full name
                        FirebaseDbManager.updateProfileField(
                            userId = user.uid,
                            fieldName = "fullName",
                            value = fullName,
                            onSuccess = {
                                showLoading(false)
                                // Navigate to profile setup since new user
                                navigateToProfileSetup()
                            },
                            onError = { error ->
                                showLoading(false)
                                // Still navigate even if name update fails
                                navigateToProfileSetup()
                            }
                        )
                    },
                    onError = { error ->
                        showLoading(false)
                        showError(error)
                    }
                )
            },
            onError = { error ->
                showLoading(false)
                showError(error)
            }
        )
    }

    private fun handleForgotPassword() {
        val email = etEmail.text.toString().trim()

        if (email.isEmpty()) {
            tilEmail.error = getString(R.string.error_email_required)
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_invalid_email)
            return
        }

        showLoading(true)

        FirebaseAuthManager.sendPasswordResetEmail(
            email = email,
            onSuccess = {
                showLoading(false)
                Toast.makeText(
                    this,
                    getString(R.string.password_reset_sent),
                    Toast.LENGTH_LONG
                ).show()
            },
            onError = { error ->
                showLoading(false)
                showError(error)
            }
        )
    }

    private fun validateLoginInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            tilEmail.error = getString(R.string.error_email_required)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        } else {
            tilEmail.error = null
        }

        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else {
            tilPassword.error = null
        }

        return isValid
    }

    private fun validateSignUpInput(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        if (fullName.isEmpty()) {
            tilFullName.error = getString(R.string.error_name_required)
            isValid = false
        } else if (fullName.length < 2) {
            tilFullName.error = getString(R.string.error_name_too_short)
            isValid = false
        } else {
            tilFullName.error = null
        }

        if (email.isEmpty()) {
            tilEmail.error = getString(R.string.error_email_required)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        } else {
            tilEmail.error = null
        }

        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else if (password.length < 6) {
            tilPassword.error = getString(R.string.error_password_too_short)
            isValid = false
        } else {
            tilPassword.error = null
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = getString(R.string.error_confirm_password_required)
            isValid = false
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = getString(R.string.error_passwords_dont_match)
            isValid = false
        } else {
            tilConfirmPassword.error = null
        }

        return isValid
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnAction.isEnabled = !isLoading
        btnAction.alpha = if (isLoading) 0.5f else 1f
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun clearErrors() {
        tvError.visibility = View.GONE
        tilFullName.error = null
        tilEmail.error = null
        tilPassword.error = null
        tilConfirmPassword.error = null
    }

    private fun checkProfileAndNavigate() {
        val userId = FirebaseAuthManager.currentUserId
        if (userId == null) {
            // Not logged in, stay here
            return
        }

        // Only show loading if views are initialized
        if (::progressBar.isInitialized) {
            showLoading(true)
        }

        FirebaseDbManager.isProfileCompleted(userId) { isCompleted ->
            if (::progressBar.isInitialized) {
                showLoading(false)
            }
            if (isCompleted) {
                navigateToMain()
            } else {
                navigateToProfileSetup()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToProfileSetup() {
        val intent = Intent(this, UserProfileSetupActivity::class.java)
        startActivity(intent)
        finish()
    }
}
