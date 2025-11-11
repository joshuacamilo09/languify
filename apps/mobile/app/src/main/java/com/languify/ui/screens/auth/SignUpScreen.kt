package com.languify.ui.screens.auth

// Importações
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.languify.R
import com.languify.ui.viewmodel.AuthViewModel
import com.languify.domain.usecase.Result
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,          // Navegação entre ecrãs
    authViewModel: AuthViewModel           // Lógica de registo (signup)
) {
    // Campos do formulário
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estados de visibilidade da password
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Mensagem de erro
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Observa estado do registo
    val scope = rememberCoroutineScope()
    val registerState by authViewModel.registerState.collectAsState()

    // Estrutura principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Logo da app
            Image(
                painter = painterResource(id = R.drawable.languify_logo),
                contentDescription = "Languify logo",
                modifier = Modifier.size(90.dp)
            )

            // Título
            Text(
                text = "Create Your Languify Account",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Nome completo
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            // Password com botão de visibilidade
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Confirmar Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(icon, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Mostra erro se as passwords não coincidirem
            errorMessage?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
            }

            // Botão de Sign Up
            Button(
                onClick = {
                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }

                    errorMessage = null
                    scope.launch {
                        authViewModel.register(name, email, password, "en")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
            ) {
                when (registerState) {
                    is Result.Loading -> CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    is Result.Success -> Text("Sign Up", fontWeight = FontWeight.Bold)
                    is Result.Error -> Text("Try Again", fontWeight = FontWeight.Bold)
                }
            }

            // Evita navegação duplicada
            val hasNavigated = remember { mutableStateOf(false) }

            // Redireciona automaticamente após registo
            LaunchedEffect(registerState) {
                if (registerState is Result.Success && !hasNavigated.value) {
                    hasNavigated.value = true
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            }

            // Link para voltar ao login
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Already have an account? Log in", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
