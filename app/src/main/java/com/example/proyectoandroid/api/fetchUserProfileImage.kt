package com.example.proyectoandroid.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun fetchUserProfileImage(): String? {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    if (uid != null) {
        val firestore = FirebaseFirestore.getInstance()
        val document = firestore.collection("users").document(uid).get().await()
        return document.getString("profileImageUrl")
    }
    return null
}
