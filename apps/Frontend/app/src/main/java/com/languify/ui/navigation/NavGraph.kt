package com.languify.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.languify.ui.screens.home.HomeScreen
import com.languify.ui.screens.history.HistoryScreen
import com.languify.ui.screens.map.MapScreen
import com.languify.ui.screens.profile.ProfileScreen
import com.languify.ui.screens.splash.SplashScreen

@Composable
fun LanguifyNavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = "splash" // ✅ Splash first
    ) {
        composable("splash") { SplashScreen(navController) }
        composable("home") { HomeScreen() }
        composable("history") { HistoryScreen() }
        composable("map") { MapScreen() }
        composable("profile") { ProfileScreen() }
    }
}
