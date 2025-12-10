package com.languify.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.languify.viewmodel.HomeViewModel
import com.languify.viewmodel.HistoryViewModel
import com.languify.viewmodel.ProfileViewModel
import com.languify.viewmodel.RecorderState
import com.languify.viewmodel.Translation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel(),
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val state by homeViewModel.state.collectAsState()
    val text by homeViewModel.transcribedText.collectAsState()

    // INICIALIZAÇÃO E LIGAÇÃO ENTRE VIEWMODELS
    LaunchedEffect(Unit) {
        homeViewModel.initialize()

        // Sempre que o Home reconhecer texto, mandamos para o Histórico
        homeViewModel.onTextRecognized = { recognizedString ->
            historyViewModel.addTranslation(
                Translation(
                    original = recognizedString,
                    translated = recognizedString // Por enquanto é igual, depois será a tradução real
                )
            )
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) homeViewModel.startRecording()
        else Toast.makeText(context, "Permission needed", Toast.LENGTH_SHORT).show()
    }

    fun handleMainButtonClick() {
        when (state) {
            RecorderState.READY -> {
                val permission = Manifest.permission.RECORD_AUDIO
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    homeViewModel.startRecording()
                } else {
                    permissionLauncher.launch(permission)
                }
            }
            RecorderState.RECORDING -> homeViewModel.stopRecording()
            RecorderState.PLAYING -> { /* Opcional */ }
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Home") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // MOSTRAR O TEXTO RECONHECIDO
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            // Botão Principal
            FloatingActionButton(
                onClick = { handleMainButtonClick() },
                modifier = Modifier.size(90.dp),
                shape = CircleShape,
                containerColor = when (state) {
                    RecorderState.READY -> MaterialTheme.colorScheme.primary
                    RecorderState.RECORDING -> MaterialTheme.colorScheme.error
                    RecorderState.PLAYING -> MaterialTheme.colorScheme.tertiary
                }
            ) {
                when (state) {
                    RecorderState.READY -> Icon(Icons.Default.Mic, "Record", tint = Color.White, modifier = Modifier.size(32.dp))
                    RecorderState.RECORDING -> Icon(Icons.Default.Stop, "Stop", tint = Color.White, modifier = Modifier.size(32.dp))
                    RecorderState.PLAYING -> Icon(Icons.Default.VolumeUp, "Playing", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = when(state) {
                    RecorderState.READY -> "Tap to Speak"
                    RecorderState.RECORDING -> "Listening..."
                    RecorderState.PLAYING -> "Speaking back..."
                },
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Preview simples apenas para layout
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Preview Mode")
    }
}