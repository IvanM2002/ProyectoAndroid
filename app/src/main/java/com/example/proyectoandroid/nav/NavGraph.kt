package com.example.proyectoandroid.nav

import UserProfileScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectoandroid.ui.AddItemScreen
import com.example.proyectoandroid.ui.LoginScreen
import com.example.proyectoandroid.ui.RegisterScreen
import com.example.proyectoandroid.ui.ShoppingListScreen
import com.example.proyectoandroid.viewmodel.ShoppingViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(navController: NavHostController, viewModel: ShoppingViewModel) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val isUserAuthenticated = firebaseAuth.currentUser != null

    LaunchedEffect(isUserAuthenticated) {
        // Redirigir a la pantalla adecuada basado en la autenticación
        if (isUserAuthenticated) {
            navController.navigate("shopping_list") {
                popUpTo(0) // Eliminar la pila para evitar volver a la pantalla de inicio de sesión
            }
        } else {
            navController.navigate("login") {
                popUpTo(0)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isUserAuthenticated) "shopping_list" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("shopping_list") {
                        popUpTo(0)
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("shopping_list") {
                        popUpTo(0)
                    }
                }
            )
        }
        composable("shopping_list") {
            ShoppingListScreen(
                viewModel = viewModel,
                onAddItemClicked = { navController.navigate("add_item") },
                onUserProfileClicked = {
                    navController.navigate("user_profile")
                }
            )
        }
        composable("add_item") {
            AddItemScreen(
                viewModel = hiltViewModel(),
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable("user_profile") {
            UserProfileScreen(onLogout = {
                navController.navigate("login") {
                    popUpTo(0)
                }
            })
        }
    }
}