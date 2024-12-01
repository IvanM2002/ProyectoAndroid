package com.example.proyectoandroid.api

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

suspend fun updateProfileImage(
    uri: Uri,
    uid: String?,
    firestore: FirebaseFirestore,
    storage: FirebaseStorage,
    onImageUpdated: (String) -> Unit
) {
    if (uid == null) return
    try {
        val storageRef = storage.reference.child("profile_images/$uid-${System.currentTimeMillis()}.jpg")
        val uploadTask = storageRef.putFile(uri).await()
        val downloadUrl = storageRef.downloadUrl.await()

        // Update Firestore with the new image URL
        firestore.collection("users").document(uid).update("profileImageUrl", downloadUrl.toString()).await()
        onImageUpdated(downloadUrl.toString())
    } catch (e: Exception) {
        println("Error updating profile image: ${e.message}")
    }
}