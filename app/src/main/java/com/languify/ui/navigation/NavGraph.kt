package com.languify.ui.navigation

import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.languify.ui.screens.auth.LoginScreen
import com.languify.ui.screens.auth.SignUpScreen
import com.languify.ui.screens.home.HomeScreen
import com.languify.ui.screens.history.HistoryScreen
import com.languify.ui.screens.map.MapScreen
import com.languify.ui.screens.profile.ProfileScreen
import com.languify.ui.screens.splash.SplashScreen
import com.languify.viewmodel.ProfileViewModel

// 🧩 imports necessários para animações
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

/**
 * Controla toda a navegação da aplicação.
 * Inclui animações suaves e bloqueio de ecrãs não autenticados.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LanguifyNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    profileViewModel: ProfileViewModel
) {
    // Estado de login proveniente do DataStore
    val isLoggedIn = profileViewModel.isLoggedIn.collectAsState()

    // 🔹 O Splash é sempre a primeira tela exibida
    AnimatedNavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = {
            fadeIn(animationSpec = tween(500)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, tween(500)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(400)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, tween(400)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(400)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, tween(400)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(350)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, tween(350)
            )
        }
    ) {
        // --- SPLASH SCREEN ---
        composable("splash") {
            SplashScreen(navController = navController, profileViewModel = profileViewModel)
        }

        // --- TELAS PÚBLICAS ---
        composable("login") {
            LoginScreen(navController, profileViewModel)
        }

        composable("signup") {
            SignUpScreen(navController)
        }

        // --- TELAS PROTEGIDAS ---
        composable("home") {
            if (isLoggedIn.value)
                HomeScreen()
            else
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
        }

        composable("history") {
            if (isLoggedIn.value)
                HistoryScreen()
            else
                navController.navigate("login") {
                    popUpTo("history") { inclusive = true }
                    launchSingleTop = true
                }
        }

        composable("map") {
            if (isLoggedIn.value)
                MapScreen()
            else
                navController.navigate("login") {
                    popUpTo("map") { inclusive = true }
                    launchSingleTop = true
                }
        }

        composable("profile") {
            if (isLoggedIn.value)
                ProfileScreen(navController = navController, profileViewModel = profileViewModel)
            else
                navController.navigate("login") {
                    popUpTo("profile") { inclusive = true }
                    launchSingleTop = true
                }
        }
    }
}
