package com.languify.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.languify.domain.usecase.Result
import com.languify.ui.viewmodel.AuthViewModel
import com.languify.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,              // Controla a navegação entre ecrãs
    profileViewModel: ProfileViewModel,        // ViewModel do perfil (para guardar token e dados do user)
    authViewModel: AuthViewModel               // ViewModel da autenticação (para login)
) {
    // Estados locais para capturar input do utilizador
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // CoroutineScope para executar tarefas assíncronas (login)
    val scope = rememberCoroutineScope()

    // Estados de visibilidade da password
    var passwordVisible by remember { mutableStateOf(false) }

    // Observa o estado do login (Loading, Success ou Error)
    val loginState by authViewModel.loginState.collectAsState()

    // Layout principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Coluna com todos os elementos
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Título principal
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Campo de e-mail
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = null)
                    }
                }
            )

            // Mensagem de erro (se existir)
            errorMessage?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
            }

            // Botão de Login
            Button(
                onClick = {
                    // Validação simples
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter email and password"
                        return@Button
                    }

                    // Chama a função de login no ViewModel
                    errorMessage = null
                    scope.launch {
                        authViewModel.login(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)) // Roxo do tema
            ) {
                // Mostra loading enquanto faz o login
                when (loginState) {
                    is Result.Loading -> CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else -> Text("Login", fontWeight = FontWeight.Bold)
                }
            }

            // Controla se já navegou para evitar múltiplos redirects
            val hasNavigated = remember { mutableStateOf(false) }

            // Efeito que reage ao resultado do login
            LaunchedEffect(loginState) {
                when (loginState) {
                    is Result.Success -> {
                        if (!hasNavigated.value) {
                            hasNavigated.value = true

                            // Guarda o token e obtém o ID do utilizador
                            val token = (loginState as Result.Success<String>).data
                            profileViewModel.saveLoginData(token) {
                                profileViewModel.getUserId { id ->
                                    if (id > 0) {
                                        profileViewModel.fetchUserProfile(id)
                                    } else {
                                        println("⚠️ User ID ainda não guardado, ignorado.")
                                    }
                                }
                            }

                            // Navega para o ecrã de perfil
                            navController.navigate("profile") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    is Result.Error -> {
                        // Mostra mensagem de erro caso o login falhe
                        errorMessage = (loginState as Result.Error).message
                    }

                    else -> Unit
                }
            }

            // Link para criar conta
            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Don’t have an account? Sign Up", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
