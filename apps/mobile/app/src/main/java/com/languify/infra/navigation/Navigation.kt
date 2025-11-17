package com.languify.infra.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.languify.identity.auth.domain.AuthService
import com.languify.identity.auth.presentation.SignScreen
import com.languify.identity.auth.presentation.SignViewModelFactory
import com.languify.ui.HomeScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
  object Login : Screen("login")

  object Home : Screen("home")
}

@Composable
fun AppNavigation(
  authService: AuthService,
  signViewModelFactory: SignViewModelFactory,
  startDestination: String = Screen.Login.route,
  navController: NavHostController = rememberNavController(),
) {
  val scope = rememberCoroutineScope()

  NavHost(navController = navController, startDestination = startDestination) {
    composable(Screen.Login.route) {
      SignScreen(
        factory = signViewModelFactory,
        onSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
      )
    }

    composable(Screen.Home.route) {
      HomeScreen(
        onSignOut = {
          scope.launch {
            authService.signOut()
            navController.navigate(Screen.Login.route) { popUpTo(0) }
          }
        }
      )
    }
  }
}
