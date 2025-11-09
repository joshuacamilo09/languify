package com.languify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.languify.ui.screens.auth.LoginScreen
import com.languify.ui.screens.auth.SignUpScreen
import com.languify.ui.screens.home.HomeScreen
import com.languify.ui.screens.map.MapScreen
import com.languify.ui.screens.history.HistoryScreen
import com.languify.ui.screens.profile.ProfileScreen
import com.languify.viewmodel.ProfileViewModel
import com.languify.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                authViewModel = authViewModel
            )
        }

        composable("signup") {
            SignUpScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("home") {
            HomeScreen(profileViewModel = profileViewModel)
        }

        composable("map") {
            MapScreen()
        }

        composable("history") {
            HistoryScreen()
        }

        composable("profile") {
            ProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }
    }
}
