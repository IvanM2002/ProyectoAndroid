package com.example.proyectoandroid.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    FirebaseStorage.getInstance()
    FirebaseFirestore.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCatImage by remember { mutableStateOf<String?>(null) }
    var showCatImages by remember { mutableStateOf(false) }
    rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    if (showCatImages) {
        SelectCatImageScreen(
            onImageSelected = { imageUrl ->
                selectedCatImage = imageUrl
                showCatImages = false
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar la imagen seleccionada o un placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            ) {
                when {
                    selectedCatImage != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(selectedCatImage),
                            contentDescription = "Selected Cat Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    imageUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Placeholder Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Select From Device")
                }
                Button(onClick = { showCatImages = true }) {
                    Text("Select Cat Image")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Input
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        errorMessage = "All fields are required."
                    } else if (password != confirmPassword) {
                        errorMessage = "Passwords do not match."
                    } else {
                        isLoading = true
                        errorMessage = null
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = task.result?.user?.uid
                                    val selectedImageUrl = selectedCatImage ?: imageUri?.toString()

                                    if (uid != null && selectedImageUrl != null) {
                                        saveUserToFirestore(uid, email, selectedImageUrl)
                                        isLoading = false
                                        onRegisterSuccess()
                                    } else {
                                        errorMessage = "Please select a valid image."
                                        isLoading = false
                                    }
                                } else {
                                    errorMessage = task.exception?.localizedMessage
                                    isLoading = false
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Register")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

fun saveUserToFirestore(uid: String, email: String, profileImageUrl: String?) {
    val firestore = FirebaseFirestore.getInstance()

    val user = mapOf(
        "uid" to uid,
        "email" to email,
        "profileImageUrl" to (profileImageUrl ?: "")
    )

    firestore.collection("users")
        .document(uid)
        .set(user)
        .addOnSuccessListener {
            println("User successfully written!")
        }
        .addOnFailureListener { e ->
            println("Error writing document: ${e.message}")
        }
}