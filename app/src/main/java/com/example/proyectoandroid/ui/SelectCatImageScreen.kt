package com.example.proyectoandroid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.proyectoandroid.api.RetrofitInstance
import kotlinx.coroutines.launch
import androidx.compose.ui.res.painterResource
import com.example.proyectoandroid.R

@Composable
fun SelectCatImageScreen(
    onImageSelected: (String) -> Unit
) {
    var catImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch cat images
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.api.getCatImages()
                catImages = response.map { it.url }
                println("Fetched cat images: $catImages") // Debug log
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error fetching cat images: ${e.message}")
                isLoading = false
            }
        }
    }

    // UI
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (catImages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No images available.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(catImages.size) { index ->
                val imageUrl = catImages[index]
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onImageSelected(imageUrl) }
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = imageUrl,
                            placeholder = painterResource(id = R.drawable.placeholder_loading),
                            error = painterResource(id = R.drawable.placeholder_error)
                        ),
                        contentDescription = "Cat Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }
        }
    }
}