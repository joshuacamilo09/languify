package com.languify.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.languify.R

/**
 * Home screen: main translation interface
 * Contains mic button, detected text, and translation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var isRecording by remember { mutableStateOf(false) } // Whether mic is active
    var original by remember { mutableStateOf("") }        // Original text spoken
    var translated by remember { mutableStateOf("") }      // Translated result

    Scaffold(
        topBar = {
            // Top bar with app name
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) })
        }
    ) { padding ->
        // Main layout (vertically centered)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Show spoken/original text or hint message
            Text(
                text = if (original.isBlank()) stringResource(R.string.home_hint) else original,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Display the translated text
            Text(
                text = translated,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // Mic button simulation
            Button(
                onClick = {
                    // Toggle recording state
                    isRecording = !isRecording

                    // Fake example translation
                    if (!isRecording) {
                        original = "Olá, como estás?"
                        translated = "Hello, how are you?"
                    } else {
                        original = ""
                        translated = ""
                    }
                },
                modifier = Modifier.height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (isRecording) stringResource(R.string.btn_stop) else stringResource(R.string.btn_speak))
            }
        }
    }
}
