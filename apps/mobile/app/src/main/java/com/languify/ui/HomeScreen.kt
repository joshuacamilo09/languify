package com.languify.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onSignOut: () -> Unit, onOpenConversation: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxSize().padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(text = "Home Screen", style = MaterialTheme.typography.headlineLarge)

    Spacer(modifier = Modifier.height(32.dp))

    Text(text = "You're logged in!", style = MaterialTheme.typography.bodyLarge)

    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = onOpenConversation, modifier = Modifier.fillMaxWidth()) {
      Text("Start Real-Time Translation")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = onSignOut) { Text("Logout") }
  }
}
