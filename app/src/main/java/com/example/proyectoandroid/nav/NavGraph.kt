package com.example.proyectoandroid.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectoandroid.ui.AddItemScreen
import com.example.proyectoandroid.ui.ShoppingListScreen
import com.example.proyectoandroid.viewmodel.ShoppingViewModel

@Composable
fun NavGraph(navController: NavHostController, viewModel: ShoppingViewModel) {
    NavHost(
        navController = navController,
        startDestination = "shopping_list"
    ) {
        composable("shopping_list") {
            ShoppingListScreen(
                viewModel = viewModel,
                onAddItemClicked = { navController.navigate("add_item") },
                onUserProfileClicked = { /* Implementar navegación a perfil de usuario más adelante */ }
            )
        }
        composable("add_item") {
            AddItemScreen(
                viewModel = hiltViewModel(),
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}