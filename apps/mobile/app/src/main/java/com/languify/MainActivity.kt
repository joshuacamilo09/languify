package com.languify

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.languify.core.PreferencesManager
import com.languify.data.network.AuthController
import com.languify.data.repository.AuthRepository
import com.languify.domain.usecase.Auth.LoginUseCase
import com.languify.domain.usecase.Auth.RegisterUseCase
import com.languify.navigation.NavGraph
import com.languify.ui.navigation.BottomNavBar
import com.languify.ui.theme.LanguifyTheme
import com.languify.ui.viewmodel.AuthViewModel
import com.languify.viewmodel.ProfileViewModel
import com.languify.viewmodel.ProfileViewModelFactory
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🔹 Inicializa dependências principais
        val authRepository = AuthRepository(AuthController.api)
        val prefs = PreferencesManager(applicationContext)

        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()

            // ✅ ViewModel global do utilizador
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(context, authRepository)
            )

            // ✅ Estados observáveis
            val isDarkMode by profileViewModel.isDarkMode.collectAsState(initial = isSystemInDarkTheme())
            val language by profileViewModel.language.collectAsState(initial = "en")

            // ✅ Atualiza idioma automaticamente
            updateLocale(language)

            // 🔹 Inicializa viewmodel de autenticação
            val authViewModel = AuthViewModel(
                loginUseCase = LoginUseCase(authRepository),
                registerUseCase = RegisterUseCase(authRepository)
            )

            // 🔹 Controla a barra inferior
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // ✅ Tema global reativo
            LanguifyTheme(darkTheme = isDarkMode) {
                Scaffold(
                    bottomBar = {
                        if (currentRoute != "login" && currentRoute != "signup") {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { paddingValues ->
                    NavGraph(
                        navController = navController,
                        profileViewModel = profileViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }

    // 🌍 Atualiza o idioma da app
    private fun updateLocale(languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        return createConfigurationContext(config)
    }
}
