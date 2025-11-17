package com.languify.identity.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.languify.identity.auth.domain.SignStatus

@Composable
fun SignScreen(
  factory: SignViewModelFactory,
  viewModel: SignViewModel = viewModel(factory = factory),
  onSuccess: () -> Unit = {},
) {
  val state by viewModel.state.collectAsState()

  LaunchedEffect(state.status) { if (state.status == SignStatus.SUCCESS) onSuccess() }

  SignContent(
    state = state,
    onEmailChange = viewModel::onEmailChange,
    onPasswordChange = viewModel::onPasswordChange,
    onLoginClick = viewModel::onSignClick,
  )
}

@Composable
fun SignContent(
  state: SignState,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onLoginClick: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    SignForm(
      state = state,
      onEmailChange = onEmailChange,
      onPasswordChange = onPasswordChange,
      onLoginClick = onLoginClick,
    )
  }
}

@Composable
fun SignForm(
  state: SignState,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onLoginClick: () -> Unit,
) {
  val loading = state.status == SignStatus.LOADING

  Column(
    modifier = Modifier.fillMaxWidth().padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Sign | Languify",
      style = MaterialTheme.typography.headlineMedium,
      modifier = Modifier.padding(bottom = 16.dp),
    )

    OutlinedTextField(
      value = state.email,
      onValueChange = onEmailChange,
      label = { Text("Email") },
      modifier = Modifier.fillMaxWidth(),
      enabled = !loading,
      singleLine = true,
    )

    OutlinedTextField(
      value = state.password,
      onValueChange = onPasswordChange,
      label = { Text("Password") },
      modifier = Modifier.fillMaxWidth(),
      enabled = !loading,
      visualTransformation = PasswordVisualTransformation(),
      singleLine = true,
    )

    if (!state.error.isNullOrBlank()) {
      Text(
        text = state.error,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.fillMaxWidth()
      )
    }

    Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
      if (loading) {
        CircularProgressIndicator(
          modifier = Modifier.size(24.dp),
          color = MaterialTheme.colorScheme.onPrimary,
        )
      } else {
        Text("Sign in")
      }
    }
  }
}
