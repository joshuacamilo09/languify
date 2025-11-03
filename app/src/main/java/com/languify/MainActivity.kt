package com.languify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.languify.ui.navigation.BottomNavBar
import com.languify.ui.navigation.LanguifyNavGraph
import com.languify.ui.theme.LanguifyTheme
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.languify.viewmodel.ProfileViewModel
import com.languify.viewmodel.ProfileViewModelFactory
import androidx.lifecycle.lifecycleScope
import com.languify.core.localization.LanguageManager
import com.languify.core.localization.LocaleManager
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import java.util.Locale

/**
 * Entry point of Languify app.
 * Handles navigation, dark mode and language.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            // Cria UMA instância do ProfileViewModel (partilhada entre toda a app)
            val profileViewModel: ProfileViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = ProfileViewModelFactory(applicationContext)
                )

            // Carrega preferências salvas (tema + idioma)
            LaunchedEffect(Unit) {
                profileViewModel.loadPreferences()
            }

            // Observa o estado global do tema e idioma
            val isDarkMode by profileViewModel.isDarkMode.collectAsState()
            val isLoggedIn by profileViewModel.isLoggedIn.collectAsState()
            val languageCode by profileViewModel.language.collectAsState()

            // Atualiza o idioma do contexto dinamicamente
            val localizedContext = remember(languageCode) {
                updateLocale(languageCode)
            }

            // Tema global controlado pelo mesmo ProfileViewModel
            LanguifyTheme(darkTheme = isDarkMode) {
                Scaffold(
                    bottomBar = {
                        if (isLoggedIn)
                        { //só aparece se o user tiver feito login
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { padding ->
                    // Passa o mesmo ViewModel para o NavGraph
                    LanguifyNavGraph(
                        navController = navController,
                        paddingValues = padding,
                        profileViewModel = profileViewModel // <<---
                    )
                }
            }
        }

        // Mantém o idioma sincronizado mesmo fora do Compose
        lifecycleScope.launch {
            LanguageManager.getLanguage(applicationContext).collect { lang ->
                LocaleManager.setLocale(applicationContext, lang)
            }
        }
    }

    private fun updateLocale(languageCode: String): android.content.Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        return createConfigurationContext(config)
    }
}
