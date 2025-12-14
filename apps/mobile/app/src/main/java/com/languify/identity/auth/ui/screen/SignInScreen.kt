package com.languify.identity.auth.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.languify.identity.auth.domain.viewmodel.SignInViewModel
import com.languify.infra.api.data.model.PromiseState
import com.languify.ui.components.PromiseButton

@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onSignIn: () -> Unit,
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()

    val state by viewModel.state.collectAsState()
    val castedState = state

    val enabled = state != PromiseState.Pending

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Sign In",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            enabled = enabled,
            value = email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            enabled = enabled,
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (castedState is PromiseState.Rejected) {
            Text(
                text = castedState.message,
                fontSize = 16.sp,
                color = Color.Red,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        PromiseButton(
            state,
            onClick = { viewModel.signIn(onSignIn) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Continue") }
    }
}
