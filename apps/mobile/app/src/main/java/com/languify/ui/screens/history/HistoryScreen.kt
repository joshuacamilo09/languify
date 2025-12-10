package com.languify.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.languify.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(historyViewModel: HistoryViewModel = viewModel()) {
    val historyList by historyViewModel.historyList.collectAsState()

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("History") }) }) { padding ->
        if (historyList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No history yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList) { item ->
                    Card {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                text = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(item.timestamp),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(item.original, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(item.translated, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "History Screen")
@Composable
fun HistoryScreenPreview() {
    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("History") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listOf(1, 2 , 3)) { index ->
                Card {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = "10:30:45 15/12/2024",
                            style = MaterialTheme.typography.labelSmall
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "Hello, how are you?",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(Modifier.height(1.dp))

                        Text(
                            "Olá, como estás?",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
