package com.languify.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.languify.viewmodel.ProfileViewModel

@Composable
fun RegisterScreen(
    profileViewModel: ProfileViewModel,
    onBackToLogin: () -> Unit,
    onSignUpClick: () -> Unit // 👈 ADICIONA ESTE PARÂMETRO
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(Modifier.padding(24.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        TextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                    return@Button
                }

                // 👉 Aqui só navegamos, sem chamar o ViewModel por agora
                onSignUpClick()
            },
            enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
        ) {
            Text("Register")
        }

        errorMessage?.let { Text(it, color = Color.Red) }

        TextButton(onClick = onBackToLogin) {
            Text("Back to Login")
        }
    }
}

@Preview(showBackground = true, name = "Register Screen")
@Composable
fun RegisterScreenPreview() {
    Column(Modifier.padding(24.dp)) {
        TextField(
            value = "John Doe",
            onValueChange = {},
            label = { Text("Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = "user@example.com",
            onValueChange = {},
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = "password",
            onValueChange = {},
            label = { Text("Password") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = "password",
            onValueChange = {},
            label = { Text("Confirm Password") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {}) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {}) {
            Text("Back to Login")
        }
    }
}
