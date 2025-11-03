package com.languify.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.languify.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Tela de Login do utilizador.
 * Agora sincronizada com o estado do DataStore e navegação automática.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, profileViewModel: ProfileViewModel) {

    // Campos de entrada
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estado de carregamento
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Observa o estado de login do ViewModel
    val isLoggedIn by profileViewModel.isLoggedIn.collectAsStateWithLifecycle()

    // Guarda a primeira verificação (para evitar delay inicial)
    var hasCheckedLogin by remember {mutableStateOf(false)}


    // assim que o estado de login mudar para true, navega automaticamente
    LaunchedEffect(isLoggedIn) {
        if (!hasCheckedLogin) {
            hasCheckedLogin = true // já verificou uma vez
        } else if (isLoggedIn) {
            // Só navega depois da verificação inicial
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // Layout principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Título
            Text(
                text = "Welcome to Languify",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Campo Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // Botão de Login
            Button(
                onClick = {
                    scope.launch {
                        loading = true

                        // Simula validação e chama o método do ViewModel
                        profileViewModel.login(email, password)

                        // A navegação agora será controlada pelo LaunchedEffect acima
                        loading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !loading && email.isNotEmpty() && password.isNotEmpty()
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Login")
                }
            }

            // Link para registo
            TextButton(onClick = {
                navController.navigate("signup")
            }) {
                Text(
                    text = "Don’t have an account? Sign up",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
