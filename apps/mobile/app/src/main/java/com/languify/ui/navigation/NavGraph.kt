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
import com.languify.ui.screens.profile.ProfileScreenRoute
import com.languify.ui.screens.test.TestRealtimeScreen
import com.languify.viewmodel.ProfileViewModel
import com.languify.ui.viewmodel.AuthViewModel
import com.languify.ui.viewmodel.ChatViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel
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
            // Passa o viewModel para o HomeScreen
            HomeScreen(
                profileViewModel = profileViewModel,
                chatViewModel = chatViewModel
            )
        }

        composable("map") {
            MapScreen()
        }

        composable("history") {
            HistoryScreen()
        }

        // No teu NavGraph.kt
        composable("profile") {
            // ProfileScreen( ... )  <- ANTIGO
            ProfileScreenRoute( // <- NOVO
                navController = navController,
                profileViewModel = profileViewModel
            )
        }

        composable("test_realtime") {
            TestRealtimeScreen(viewModel = chatViewModel)
        }
    }
}