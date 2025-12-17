package com.example.ai_fitness_workout_manager.firebase

import com.example.ai_fitness_workout_manager.model.UserProfile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseDbManager {

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://ai-fitness-tracker-manager-default-rtdb.firebaseio.com/")
    }

    private val usersRef by lazy { database.getReference("users") }

    fun createUserProfile(
        userId: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val initialProfile = UserProfile(
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val userData = mapOf(
            "email" to email,
            "profile" to initialProfile.toMap()
        )

        usersRef.child(userId).setValue(userData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to create user profile") }
    }

    fun updateUserProfile(
        userId: String,
        profile: UserProfile,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val updatedProfile = profile.copy(updatedAt = System.currentTimeMillis())

        usersRef.child(userId).child("profile").setValue(updatedProfile.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to update profile") }
    }

    fun getUserProfile(
        userId: String,
        onSuccess: (UserProfile?) -> Unit,
        onError: (String) -> Unit
    ) {
        usersRef.child(userId).child("profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profile = snapshot.getValue(UserProfile::class.java)
                    onSuccess(profile)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    fun isProfileCompleted(
        userId: String,
        onResult: (Boolean) -> Unit
    ) {
        usersRef.child(userId).child("profile").child("profileCompleted")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isCompleted = snapshot.getValue(Boolean::class.java) ?: false
                    onResult(isCompleted)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(false)
                }
            })
    }

    fun markProfileAsCompleted(
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val updates = mapOf(
            "profileCompleted" to true,
            "updatedAt" to System.currentTimeMillis()
        )

        usersRef.child(userId).child("profile").updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to update profile") }
    }

    fun updateProfileField(
        userId: String,
        fieldName: String,
        value: Any,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val updates = mapOf(
            fieldName to value,
            "updatedAt" to System.currentTimeMillis()
        )

        usersRef.child(userId).child("profile").updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to update field") }
    }
}
