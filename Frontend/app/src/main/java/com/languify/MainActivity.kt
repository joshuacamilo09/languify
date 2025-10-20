package com.languify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.languify.ui.navigation.BottomNavBar
import com.languify.ui.navigation.LanguifyNavGraph
import com.languify.ui.theme.LanguifyTheme
import androidx.activity.enableEdgeToEdge // Import this
import androidx.compose.runtime.collectAsState
import com.languify.ui.viewmodel.ProfileViewModel
import com.languify.ui.viewmodel.ProfileViewModelFactory
import androidx.lifecycle.lifecycleScope
import com.languify.core.localization.LanguageManager
import com.languify.core.localization.LocaleManager
import kotlinx.coroutines.launch

/**
 * The entry point of the app.
 * It sets up the app theme, navigation controller, and bottom navigation bar.
 */


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Enable edge-to-edge display
        enableEdgeToEdge()

        setContent {
            LanguifyTheme {
                val navController = rememberNavController()
                val profileViewModel: ProfileViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = ProfileViewModelFactory(applicationContext))

                // Observe dark mode value
                val isDarkMode = profileViewModel.darkModeEnabled.collectAsState().value

                LanguifyTheme(darkTheme = isDarkMode) {
                    Scaffold(
                        bottomBar = { BottomNavBar(navController = navController) }
                    ) { padding ->
                        // When edge-to-edge is enabled, the 'padding' from Scaffold
                        // will correctly include system window insets,
                        // making point #1 even more important.
                        LanguifyNavGraph(navController = navController, paddingValues = padding)
                    }
                }
            }
        }
        lifecycleScope.launch {
            LanguageManager.getLanguage(applicationContext).collect { lang ->
                LocaleManager.setLocale(applicationContext, lang)
            }
        }
    }
}

