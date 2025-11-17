package com.languify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.languify.identity.auth.data.ApiAuthRepository
import com.languify.identity.auth.domain.AuthService
import com.languify.identity.auth.presentation.SignViewModelFactory
import com.languify.infra.api.RetrofitClient
import com.languify.infra.navigation.AppNavigation
import com.languify.infra.navigation.Screen
import com.languify.infra.security.TokenStorage
import com.languify.infra.websocket.WebSocketService
import com.languify.ui.theme.LanguifyTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val tokenStorage = TokenStorage(applicationContext)
    val api = RetrofitClient.create(tokenStorage)
    val authRepository = ApiAuthRepository(api, tokenStorage)
    val viewModelFactory = SignViewModelFactory(authRepository)

    val webSocketService = WebSocketService(tokenStorage)
    val authService = AuthService(tokenStorage, webSocketService)

    setContent {
      LanguifyTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          var loading by remember { mutableStateOf(true) }
          var startDestination by remember { mutableStateOf(Screen.Login.route) }

          LaunchedEffect(Unit) {
            val isAuthenticated = authService.isAuthenticated()
            startDestination = if (isAuthenticated) Screen.Home.route else Screen.Login.route

            if (isAuthenticated) authService.onSign()
            loading = false
          }

          if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              CircularProgressIndicator()
            }
          } else {
            AppNavigation(
              authService = authService,
              signViewModelFactory = viewModelFactory,
              startDestination = startDestination,
            )
          }
        }
      }
    }
  }
}
