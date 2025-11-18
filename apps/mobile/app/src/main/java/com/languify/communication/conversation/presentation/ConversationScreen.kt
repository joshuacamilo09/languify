package com.languify.communication.conversation.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.languify.communication.conversation.domain.RecordingState
import com.languify.communication.conversation.domain.TranslationState

@Composable
fun ConversationScreen(viewModel: ConversationViewModel) {
  val conversation by viewModel.conversation.collectAsState()
  val isStarting by viewModel.isStarting.collectAsState()
  val context = LocalContext.current

  val permissionLauncher =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
      isGranted ->
      if (isGranted) viewModel.startRecording()
    }

  val onStartRecording = {
    val hasPermission =
      ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
        android.content.pm.PackageManager.PERMISSION_GRANTED

    if (hasPermission) viewModel.startRecording()
    else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
  }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    if (conversation == null) {
      StartConversationView(isStarting = isStarting, onStart = { viewModel.startConversation() })
    } else {
      ActiveConversationView(
        fromLanguage = conversation!!.fromLanguage,
        toLanguage = conversation!!.toLanguage,
        recordingState = conversation!!.recordingState,
        translationState = conversation!!.translationState,
        onStartRecording = onStartRecording,
        onStopRecording = { viewModel.stopRecording() },
        onEndConversation = { viewModel.endConversation() },
      )
    }
  }
}

@Composable
fun StartConversationView(isStarting: Boolean, onStart: () -> Unit) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text("Start Real-Time Translation", style = MaterialTheme.typography.headlineMedium)

    if (isStarting) {
      CircularProgressIndicator()
      Text("Connecting...", style = MaterialTheme.typography.bodyMedium)
    } else {
      Button(onClick = onStart) { Text("Start Conversation") }
    }
  }
}

@Composable
fun ActiveConversationView(
  fromLanguage: String,
  toLanguage: String,
  recordingState: RecordingState,
  translationState: TranslationState,
  onStartRecording: () -> Unit,
  onStopRecording: () -> Unit,
  onEndConversation: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize().padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(24.dp),
  ) {
    Text("Active Conversation", style = MaterialTheme.typography.headlineMedium)

    LanguageIndicator(from = fromLanguage, to = toLanguage)

    Spacer(modifier = Modifier.weight(1f))

    TranslationStateIndicator(state = translationState)

    RecordingButton(
      recordingState = recordingState,
      translationState = translationState,
      onStartRecording = onStartRecording,
      onStopRecording = onStopRecording,
    )

    Spacer(modifier = Modifier.weight(1f))

    Button(
      onClick = onEndConversation,
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
    ) {
      Text("End Conversation")
    }
  }
}

@Composable
fun LanguageIndicator(from: String, to: String) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(from.uppercase(), style = MaterialTheme.typography.titleLarge)
    Text("→", style = MaterialTheme.typography.titleLarge)
    Text(to.uppercase(), style = MaterialTheme.typography.titleLarge)
  }
}

@Composable
fun TranslationStateIndicator(state: TranslationState) {
  val text =
    when (state) {
      TranslationState.IDLE -> "Ready to start"
      TranslationState.LOADING -> "Processing audio..."
      TranslationState.REPRODUCING -> "Playing translation"
      TranslationState.DONE -> "Translation complete"
      TranslationState.READY -> "Ready for next speaker"
    }

  val color =
    when (state) {
      TranslationState.LOADING,
      TranslationState.REPRODUCING -> MaterialTheme.colorScheme.primary
      TranslationState.READY -> MaterialTheme.colorScheme.tertiary
      else -> MaterialTheme.colorScheme.onSurface
    }

  Text(text = text, style = MaterialTheme.typography.bodyLarge, color = color)
}

@Composable
fun RecordingButton(
  recordingState: RecordingState,
  translationState: TranslationState,
  onStartRecording: () -> Unit,
  onStopRecording: () -> Unit,
) {
  val isEnabled =
    translationState == TranslationState.READY || recordingState == RecordingState.RECORDING

  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    when (recordingState) {
      RecordingState.RECORDING -> {
        FloatingActionButton(
          onClick = onStopRecording,
          containerColor = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(80.dp),
        ) {
          Icon(
            Icons.Default.Stop,
            contentDescription = "Stop Recording",
            modifier = Modifier.size(40.dp),
          )
        }
        Text(
          "Recording...",
          color = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(top = 8.dp),
        )
      }
      RecordingState.PROCESSING -> {
        CircularProgressIndicator(modifier = Modifier.size(80.dp))
        Text("Processing...", modifier = Modifier.padding(top = 8.dp))
      }
      else -> {
        FloatingActionButton(
          onClick = onStartRecording,
          modifier = Modifier.size(80.dp),
          containerColor =
            if (isEnabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
        ) {
          Icon(
            Icons.Default.Mic,
            contentDescription = "Start Recording",
            modifier = Modifier.size(40.dp),
            tint =
              if (isEnabled) MaterialTheme.colorScheme.onPrimary
              else MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}
