import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.proyectoandroid.api.updateProfileImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun UserProfileScreen(onLogout: () -> Unit) {
    var userEmail by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
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
                    updateProfileImage(uri, uid, firestore, storage) { newImageUrl ->
                        profileImageUrl = newImageUrl
                    }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile image
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

        // Change profile picture
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Change Profile Picture")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Change password
        Button(
            onClick = {
                coroutineScope.launch {
                    val email = firebaseAuth.currentUser?.email
                    if (!email.isNullOrEmpty()) {
                        try {
                            firebaseAuth.sendPasswordResetEmail(email).await()
                            println("Password reset email sent to $email")
                        } catch (e: Exception) {
                            println("Error sending password reset email: ${e.message}")
                        }
                    }
                }
            }
        ) {
            Text("Change Password")
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

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}