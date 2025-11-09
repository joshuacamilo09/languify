package com.languify.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.languify.domain.usecase.Result
import com.languify.ui.viewmodel.AuthViewModel
import com.languify.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val loginState by authViewModel.loginState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter email and password"
                        return@Button
                    }

                    errorMessage = null
                    scope.launch {
                        authViewModel.login(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
            ) {
                when (loginState) {
                    is Result.Loading -> CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else -> Text("Login", fontWeight = FontWeight.Bold)
                }
            }

            // 🔹 Navegação automática após sucesso
            val hasNavigated = remember { mutableStateOf(false) }

            LaunchedEffect(loginState) {
                when (loginState) {
                    is Result.Success -> {
                        if (!hasNavigated.value) {
                            hasNavigated.value = true

                            // Extrair token e ID (caso o backend retorne ambos)
                            val token = (loginState as Result.Success<String>).data
                            profileViewModel.saveLoginData(token) {
                                profileViewModel.getUserId { id ->
                                    if (id > 0) {
                                        profileViewModel.fetchUserProfile(id)
                                    } else {
                                        // fallback: tentar buscar userId a partir do backend se ainda não existir
                                        println("⚠️ User ID ainda não guardado, ignorado.")
                                    }
                                }
                            }

                            navController.navigate("profile") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    is Result.Error -> {
                        errorMessage = (loginState as Result.Error).message
                    }

                    else -> Unit
                }
            }


            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Don’t have an account? Sign Up", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
