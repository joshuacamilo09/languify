package com.languify.infra.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.languify.identity.auth.data.model.AuthState
import com.languify.identity.auth.data.model.LocalSession
import com.languify.identity.auth.domain.viewmodel.AuthViewModel
import com.languify.identity.auth.domain.viewmodel.SignInViewModel
import com.languify.identity.auth.domain.viewmodel.SignUpViewModel
import com.languify.identity.auth.ui.screen.SignInScreen
import com.languify.identity.auth.ui.screen.SignUpScreen
import com.languify.infra.navigation.data.model.Route
import com.languify.ui.screen.HomeScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.state.collectAsState()

    when (val castedAuthState = authState) {
        AuthState.Pending ->
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    color = LocalContentColor.current,
                    modifier = Modifier.size(24.dp),
                )
            }

        AuthState.Unauthenticated ->
            NavHost(
                navController = navController,
                startDestination = Route.SignIn.path,
            ) {
                composable(Route.SignIn.path) {
                    val viewModel: SignInViewModel = viewModel()
                    SignInScreen(viewModel, onSignIn = { authViewModel.validateSession() })
                }

                composable(Route.SignUp.path) {
                    val viewModel: SignUpViewModel = viewModel()
                    SignUpScreen(viewModel, onSignUp = { authViewModel.validateSession() })
                }
            }

        is AuthState.Authenticated ->
            CompositionLocalProvider(LocalSession provides castedAuthState.session) {
                NavHost(
                    navController = navController,
                    startDestination = Route.Home.path,
                ) { composable(Route.Home.path) { HomeScreen(authViewModel) } }
            }
    }
}
