package com.languify.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.languify.viewmodel.ProfileViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val user by profileViewModel.user.collectAsStateWithLifecycle()
    val isDarkMode by profileViewModel.isDarkMode.collectAsStateWithLifecycle()

    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        profileViewModel.getUserId { id ->
            if (id != -1L) {
                coroutineScope.launch {
                    profileViewModel.fetchUserProfile(id)
                }
            } else {
                println("⚠️ Nenhum ID encontrado no DataStore.")
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                actions = {
                    IconButton(onClick = { profileViewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()

                user != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("👋 Bem-vindo, ${user?.nome ?: "Utilizador"}!", style = MaterialTheme.typography.headlineSmall)
                        Text("📧 ${user?.email ?: "No email"}")
                        Text("🌐 Native: ${user?.native_idiom ?: "Not defined"}")
                        Text("🗓️ Joined: ${user?.RegisterDate ?: "Unknown date"}")

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                profileViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        ) {
                            Text("Logout")
                        }
                    }
                }

                else -> Text("Unable to load profile data", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
