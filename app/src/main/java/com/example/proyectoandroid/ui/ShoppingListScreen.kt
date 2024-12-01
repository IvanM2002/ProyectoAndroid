package com.example.proyectoandroid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.proyectoandroid.viewmodel.ShoppingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ShoppingViewModel,
    onAddItemClicked: () -> Unit,
    onUserProfileClicked: () -> Unit
) {
    val items = viewModel.shoppingItems.collectAsState(emptyList())
    val userProfileImageUrl = remember { mutableStateOf<String?>(null) }
    val firestore = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    // Cargar la imagen del perfil del usuario desde Firestore
    LaunchedEffect(uid) {
        uid?.let {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    userProfileImageUrl.value = document.getString("profileImageUrl")
                }
                .addOnFailureListener { e ->
                    println("Error fetching profile image: ${e.message}")
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping List") },
                actions = {
                    IconButton(onClick = onUserProfileClicked) {
                        if (userProfileImageUrl.value != null) {
                            Image(
                                painter = rememberAsyncImagePainter(userProfileImageUrl.value),
                                contentDescription = "User Profile",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItemClicked) {
                Text("+")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(items.value) { item ->
                ShoppingItemCard(item = item, onDelete = { viewModel.deleteItem(it) })
            }
        }
    }
}