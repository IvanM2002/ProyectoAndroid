import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.proyectoandroid.api.updateProfileImage
import com.example.proyectoandroid.api.updateProfileImageFromUrl
import com.example.proyectoandroid.ui.SelectCatImageScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun UserProfileScreen(onLogout: () -> Unit) {
    var userEmail by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showCatImages by remember { mutableStateOf(false) }
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val uid = firebaseAuth.currentUser?.uid
    val coroutineScope = rememberCoroutineScope()

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    updateProfileImage(
                        uri = uri,
                        uid = FirebaseAuth.getInstance().currentUser?.uid,
                        firestore = FirebaseFirestore.getInstance(),
                        storage = FirebaseStorage.getInstance(),
                        onImageUpdated = { newImageUrl ->
                            profileImageUrl = newImageUrl
                            println("Profile image updated successfully: $newImageUrl")
                        },
                        onError = { errorMessage ->
                            println("Error: $errorMessage")
                        }
                    )
                }
            }
        }
    )

    // Fetch user data
    LaunchedEffect(uid) {
        uid?.let {
            try {
                val document = firestore.collection("users").document(uid).get().await()
                userEmail = document.getString("email") ?: "No email available"
                profileImageUrl = document.getString("profileImageUrl") ?: ""
            } catch (e: Exception) {
                println("Error fetching user profile: ${e.message}")
            }
        }
    }

    if (showCatImages) {
        // Mostrar la pantalla de selección de imágenes de gatos
        SelectCatImageScreen(onImageSelected = { catImageUrl ->
            coroutineScope.launch {
                updateProfileImageFromUrl(catImageUrl, uid, firestore) { newImageUrl ->
                    profileImageUrl = newImageUrl
                    showCatImages = false
                }
            }
        })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mostrar imagen de perfil
            if (profileImageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUrl),
                    contentDescription = "User Profile Image",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Email: $userEmail", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para actualizar foto de perfil
            Button(onClick = { showOptionsDialog = true }) {
                Text("Update Profile Picture")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout
            Button(
                onClick = {
                    firebaseAuth.signOut()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Logout")
            }
        }
    }

    // Diálogo para opciones de imagen
    if (showOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showOptionsDialog = false },
            title = { Text("Update Profile Picture") },
            text = { Text("Choose how to update your profile picture:") },
            confirmButton = {
                TextButton(onClick = {
                    showOptionsDialog = false
                    imagePickerLauncher.launch("image/*")
                }) {
                    Text("From Device")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showOptionsDialog = false
                    showCatImages = true
                }) {
                    Text("Cat Images")
                }
            }
        )
    }
}