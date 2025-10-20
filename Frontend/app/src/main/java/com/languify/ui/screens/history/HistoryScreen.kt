package com.languify.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.languify.R

// Simple data model for one history entry
data class HistoryItem(val date: String, val original: String, val translated: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    // Remember a local list of fake history items
    val items = remember {
        mutableStateListOf(
            HistoryItem("2025-10-17 11:30", "Bom dia!", "Good morning!"),
            HistoryItem("2025-10-17 11:35", "Onde é a estação?", "Where is the station?")
        )
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(R.string.tab_history)) }) }
    ) { padding ->
        if (items.isEmpty()) {
            // Show message when history is empty
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(stringResource(R.string.history_empty)) }
        } else {
            // List all translation entries
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { h ->
                    Card {
                        Column(Modifier.padding(12.dp)) {
                            Text(h.date, style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.height(6.dp))
                            Text("🗣️ " + h.original, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(4.dp))
                            Text("🌍 " + h.translated, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
