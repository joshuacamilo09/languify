package com.languify

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.languify.core.PreferencesManager
import com.languify.core.localization.LocaleManager
import com.languify.data.network.AuthController
import com.languify.data.network.RealtimeClient
import com.languify.data.repository.AuthRepository
import com.languify.data.repository.ChatRepository
import com.languify.domain.usecase.Auth.LoginUseCase
import com.languify.domain.usecase.Auth.RegisterUseCase
import com.languify.domain.usecase.*
import com.languify.domain.usecase.CreateChatUseCase
import com.languify.domain.usecase.DeleteChatUseCase
import com.languify.domain.usecase.GetChatsUseCase
import com.languify.domain.usecase.SendMessageUseCase
import com.languify.navigation.NavGraph
import com.languify.ui.navigation.BottomNavBar
import com.languify.ui.theme.LanguifyTheme
import com.languify.ui.viewmodel.AuthViewModel
import com.languify.ui.viewmodel.ChatViewModel
import com.languify.viewmodel.ProfileViewModel
import com.languify.viewmodel.ProfileViewModelFactory
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Instanciar Repositórios Base
        val authRepository = AuthRepository(AuthController.api)

        // Instanciar Dependências do Chat
        // Criamos o cliente WebSocket e injetamo-lo no Repositório
        val realtimeClient = RealtimeClient()
        val chatRepository = ChatRepository(AuthController.api, realtimeClient)

        // PreferencesManager (opcional se não usado diretamente)
        val prefs = PreferencesManager(applicationContext)

        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()

            // VIEW MODELS

            // Profile
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(context, authRepository)
            )

            // Auth
            val authViewModel = AuthViewModel(
                loginUseCase = LoginUseCase(authRepository),
                registerUseCase = RegisterUseCase(authRepository)
            )

            // C. CHAT (Factory Inline - Resolve o teu problema!)
            // Como não tens ficheiro Factory, criamos uma aqui mesmo.
            val chatViewModel: ChatViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ChatViewModel(
                            CreateChatUseCase(chatRepository),
                            GetChatsUseCase(chatRepository),
                            SendMessageUseCase(chatRepository),
                            DeleteChatUseCase(chatRepository),
                            chatRepository // Injeta o repositório com WebSocket
                        ) as T
                    }
                }
            )

            // LÓGICA DE UI

            val isDarkMode by profileViewModel.isDarkMode.collectAsState(initial = isSystemInDarkTheme())
            val language by profileViewModel.language.collectAsState(initial = LocaleManager.getPersistedLanguage(context))

            LaunchedEffect(language) {
                val currentSystemLang = Locale.getDefault().language
                if (language.isNotEmpty() && language != currentSystemLang) {
                    LocaleManager.setNewLocale(this@MainActivity, language)
                    recreate()
                }
            }

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

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
                        authViewModel = authViewModel,
                        chatViewModel = chatViewModel // Passa o ChatViewModel para o NavGraph
                    )
                }
            }
        }
    }
}