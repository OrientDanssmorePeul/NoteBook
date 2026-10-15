package com.example.notebook.core

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

object AuthService {
    private val auth = FirebaseAuth.getInstance()

    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun register(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    onResult(true, null)
                } else {
                    Log.e(
                        "FirebaseAuth",
                        "Registration failed",
                        task.exception
                    )
                }
            }
    }

    fun isUserLoggedIn() = auth.currentUser != null

    fun getUserId() = auth.currentUser?.uid

    fun logout() {
        auth.signOut()
    }
}