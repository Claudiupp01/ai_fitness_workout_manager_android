package com.example.ai_fitness_workout_manager.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthManager {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isLoggedIn: Boolean
        get() = currentUser != null

    val currentUserId: String?
        get() = currentUser?.uid

    fun signUp(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser?.let { onSuccess(it) }
                        ?: onError("User creation failed")
                } else {
                    onError(getErrorMessage(task.exception))
                }
            }
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser?.let { onSuccess(it) }
                        ?: onError("Sign in failed")
                } else {
                    onError(getErrorMessage(task.exception))
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(getErrorMessage(task.exception))
                }
            }
    }

    private fun getErrorMessage(exception: Exception?): String {
        return when {
            exception?.message?.contains("email address is badly formatted") == true ->
                "Please enter a valid email address"
            exception?.message?.contains("password is invalid") == true ->
                "Incorrect password"
            exception?.message?.contains("no user record") == true ->
                "No account found with this email"
            exception?.message?.contains("email address is already in use") == true ->
                "An account already exists with this email"
            exception?.message?.contains("password should be at least 6 characters") == true ->
                "Password must be at least 6 characters"
            exception?.message?.contains("network error") == true ->
                "Network error. Please check your connection"
            else -> exception?.message ?: "An unexpected error occurred"
        }
    }
}
