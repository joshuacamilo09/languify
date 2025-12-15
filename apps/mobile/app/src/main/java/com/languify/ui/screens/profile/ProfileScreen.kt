package com.languify.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.languify.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
// Certifica-te que tens este import para o R (ajusta o pacote se necessário)
// import com.languify.R
import com.languify.ui.theme.LanguifyTheme // Importa o teu tema para a preview funcionar bem

// Composable "Inteligente" (Stateful)
// Este conecta-se ao ViewModel e trata da navegação.
@Composable
fun ProfileScreenRoute(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val isDarkMode by profileViewModel.isDarkMode.collectAsState()
    val currentLanguage by profileViewModel.language.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Chama a versão sem estado passando apenas os dados e as ações necessárias
    StatelessProfileScreen(
        isDarkMode = isDarkMode,
        currentLanguage = currentLanguage,
        onToggleDarkMode = { profileViewModel.toggleDarkMode() },
        onLanguageSelected = { profileViewModel.setLanguage(it) },
        onNavigateToTest = { navController.navigate("test_realtime") },
        onLogout = {
            coroutineScope.launch {
                profileViewModel.logout()
                delay(200)
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    )
}

// Composable "Burro" (Stateless) - SÓ UI
// Este recebe dados puros e lambdas. É fácil de testar e pré-visualizar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatelessProfileScreen(
    isDarkMode: Boolean,
    currentLanguage: String,
    onToggleDarkMode: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onNavigateToTest: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                actions = {
                    // Alterna entre tema claro/escuro
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Foto de perfil
            Image(
                // Usei um ícone genérico do sistema para a preview funcionar sem o teu R.drawable
                // Substitui de volta pelo teu: painterResource(id = R.drawable.ic_profile_placeholder)
                painter = painterResource(android.R.drawable.ic_menu_myplaces),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // Dados do utilizador
            Text("Alex Lima", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("@alexl", color = MaterialTheme.colorScheme.primary)
            Text("alex@email.com", style = MaterialTheme.typography.bodyMedium)

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            // Idioma
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Language", style = MaterialTheme.typography.bodyLarge)

                val languages = listOf("English", "Português", "Français", "Deutsch", "中文")
                var expanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(currentLanguage)
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    onLanguageSelected(lang)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Empurra tudo para baixo

            // BOTÃO DE TESTE OPENAI (NOVO)
            Button(
                onClick = onNavigateToTest,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), // Vermelho
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("TESTAR OPENAI REALTIME", color = Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Logout
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Logout", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

//  As Previews (Agora funcionam de verdade!)
// Usamos dados "falsos" para ver como a UI se comporta.

@Preview(showBackground = true, name = "Light Mode", group = "UI")
@Composable
fun ProfileScreenLightPreview() {
    // Envolvemos no tema da app para as cores ficarem corretas
    LanguifyTheme(darkTheme = false) {
        StatelessProfileScreen(
            isDarkMode = false,
            currentLanguage = "English",
            onToggleDarkMode = {}, // Lambdas vazias para preview
            onLanguageSelected = {},
            onNavigateToTest = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode", group = "UI", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenDarkPreview() {
    LanguifyTheme(darkTheme = true) {
        StatelessProfileScreen(
            isDarkMode = true,
            currentLanguage = "Português",
            onToggleDarkMode = {},
            onLanguageSelected = {},
            onNavigateToTest = {},
            onLogout = {}
        )
    }
}