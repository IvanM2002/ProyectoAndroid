package com.example.proyectoandroid.api

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun updateProfileImageFromUrl(
    imageUrl: String,
    uid: String?,
    firestore: FirebaseFirestore,
    onImageUpdated: (String) -> Unit
) {
    if (uid == null) return
    try {
        firestore.collection("users").document(uid).update("profileImageUrl", imageUrl).await()
        onImageUpdated(imageUrl)
    } catch (e: Exception) {
        println("Error updating profile image from URL: ${e.message}")
    }
}