package com.findresto.project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.findresto.project.navigation.NavRoutes
import com.findresto.project.screens.RestaurantsScreen

@Composable
fun NavigationHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = NavRoutes.RESTAURANTS.route,
    ) {

        composable(NavRoutes.RESTAURANTS.route) {
            RestaurantsScreen(navController)
        }

    }
}