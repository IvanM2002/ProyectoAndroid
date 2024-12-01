package com.example.proyectoandroid.api

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun saveUserToFirestore(uid: String, email: String, profileImageUrl: String?) {
    val firestore = FirebaseFirestore.getInstance()

    val user = mapOf(
        "uid" to uid,
        "email" to email,
        "profileImageUrl" to (profileImageUrl ?: "")
    )

    try {
        firestore.collection("users")
            .document(uid)
            .set(user)
            .await()
        println("User successfully written!")
    } catch (e: Exception) {
        println("Error writing document: ${e.message}")
    }
}