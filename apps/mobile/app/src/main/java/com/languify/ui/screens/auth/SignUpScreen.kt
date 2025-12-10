package com.languify.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.languify.R
import com.languify.ui.viewmodel.AuthViewModel
import com.languify.domain.usecase.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current // Necessário para mostrar Toast
    val scope = rememberCoroutineScope()

    // Observa estado do registo
    val registerState by authViewModel.registerState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // LÓGICA DE NAVEGAÇÃO E ERRO
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is Result.Success -> {
                Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                authViewModel.resetState() // Limpa o estado antes de navegar
                navController.navigate("login") {
                    popUpTo("signup") { inclusive = true }
                }
            }
            is Result.Error -> {
                // Mostra o erro que vem do Backend (ex: "Email já existe")
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

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
            Image(
                painter = painterResource(id = R.drawable.languify_logo),
                contentDescription = "Languify logo",
                modifier = Modifier.size(90.dp)
            )

            Text(
                text = "Create Your Languify Account",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // ... (Os teus TextFields continuam iguais aqui) ...
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

            // Mensagem de erro local (passwords não coincidem)
            errorMessage?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
            }

            // BOTÃO CORRIGIDO
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        errorMessage = "Please fill all fields"
                        return@Button
                    }
                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }

                    errorMessage = null
                    // Chama o registo
                    authViewModel.register(name, email, password, "en")
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                // Desativa o botão se estiver a carregar para não clicar 2 vezes
                enabled = registerState !is Result.Loading
            ) {
                if (registerState is Result.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign Up", fontWeight = FontWeight.Bold)
                }
            }

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Already have an account? Log in", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}