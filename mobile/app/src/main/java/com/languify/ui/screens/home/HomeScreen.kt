package com.languify.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.languify.viewmodel.HomeViewModel
import com.languify.viewmodel.HistoryViewModel
import com.languify.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel(),
    profileViewModel: ProfileViewModel
) {
    val isRecording by homeViewModel.isRecording.collectAsState()
    val translation by homeViewModel.translation.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Home") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isRecording) "🎙️ Listening..." else translation?.original ?: "Press the button and start speaking",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(24.dp))

            if (translation != null) {
                Text(
                    text = "🌍 ${translation!!.translated}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(24.dp))
            }

            Button(
                onClick = { homeViewModel.toggleRecording(historyViewModel) },
                modifier = Modifier.height(56.dp)
            ) {
                Text(if (isRecording) "Stop" else "Speak")
            }
        }
    }
}
