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
import com.languify.communication.conversation.domain.ConversationService
import com.languify.communication.conversation.presentation.ConversationViewModelFactory
import com.languify.identity.auth.data.ApiAuthRepository
import com.languify.identity.auth.domain.AuthService
import com.languify.identity.auth.presentation.SignViewModelFactory
import com.languify.infra.api.RetrofitClient
import com.languify.infra.health.domain.HealthService
import com.languify.infra.health.presentation.ErrorScreen
import com.languify.infra.navigation.AppNavigation
import com.languify.infra.navigation.Screen
import com.languify.infra.security.TokenStorage
import com.languify.infra.websocket.domain.WebSocketService
import com.languify.ui.theme.LanguifyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val tokenStorage = TokenStorage(applicationContext)
    val webSocketService = WebSocketService(tokenStorage)

    val healthApi = RetrofitClient.createHealthApi()
    val healthService = HealthService(healthApi)

    val authApi = RetrofitClient.createAuthApi(tokenStorage)
    val authRepository = ApiAuthRepository(authApi, tokenStorage)
    val authService = AuthService(tokenStorage, webSocketService)
    val signViewModelFactory = SignViewModelFactory(authRepository)

    val conversationService = ConversationService(webSocketService)
    val conversationViewModelFactory = ConversationViewModelFactory(conversationService)

    setContent {
      LanguifyTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          var loading by remember { mutableStateOf(true) }
          var healthy by remember { mutableStateOf(false) }
          var startDestination by remember { mutableStateOf(Screen.Login.route) }
          val scope = rememberCoroutineScope()

          fun performHealthCheck() {
            scope.launch {
              loading = true
              healthy = false

              val isHealthy = healthService.checkHealth()

              if (!isHealthy) {
                loading = false
                return@launch
              }

              healthy = true

              val isAuthenticated = authService.isAuthenticated()
              startDestination = if (isAuthenticated) Screen.Home.route else Screen.Login.route

              if (isAuthenticated) authService.onSign()
              loading = false
            }
          }

          LaunchedEffect(Unit) { performHealthCheck() }

          when {
            loading -> {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
              }
            }
            !healthy -> {
              ErrorScreen(onRetry = { performHealthCheck() })
            }
            else -> {
              LaunchedEffect(Unit) {
                webSocketService.events.collect { event -> conversationService.handleWebSocketEvent(event) }
              }

              AppNavigation(
                authService = authService,
                signViewModelFactory = signViewModelFactory,
                conversationViewModelFactory = conversationViewModelFactory,
                startDestination = startDestination,
              )
            }
          }
        }
      }
    }
  }
}
