package com.example.proyectoandroid.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun IconWithProfileImage(profileImageUrl: String) {
    Icon(
        painter = rememberAsyncImagePainter(profileImageUrl),
        contentDescription = "User Profile",
        modifier = Modifier
            .size(40.dp)
            .padding(8.dp)
            .clip(CircleShape)
    )
}