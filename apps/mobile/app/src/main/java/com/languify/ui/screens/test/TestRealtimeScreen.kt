package com.languify.ui.screens.test

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.languify.core.utils.AudioStreamManager
import com.languify.ui.viewmodel.ChatViewModel
import org.json.JSONObject

@Composable
fun TestRealtimeScreen(
    viewModel: ChatViewModel
) {
    val context = LocalContext.current
    // Gestor de Áudio (Cria e destrói com o ecrã)
    val audioManager = remember { AudioStreamManager() }

    // Estados
    var isRecording by remember { mutableStateOf(false) }
    var logs by remember { mutableStateOf("Logs aparecerão aqui...") }
    val gptResponse by viewModel.realtimeResponse.collectAsState()

    // Permissão de Microfone
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Ignoramos o resultado por agora, assume que deu sim */ }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        viewModel.startRealtimeSession()
    }

    // Processar resposta da OpenAI (Tocar Áudio)
    LaunchedEffect(gptResponse) {
        if (gptResponse.isNotEmpty()) {
            try {
                val json = JSONObject(gptResponse)
                val type = json.optString("type")

                if (type == "response.audio.delta") {
                    val delta = json.optString("delta")
                    // MOSTRA NO ECRÃ SE CHEGAR ÁUDIO
                    Toast.makeText(context, "🔊 Áudio recebido!", Toast.LENGTH_SHORT).show()
                    audioManager.playAudioChunk(delta)
                }
                else if (type == "response.audio_transcript.done") {
                    val text = json.optString("transcript")
                    logs = "🤖: $text"
                }
            } catch (e: Exception) {
                // ...
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { audioManager.release() }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Laboratório OpenAI Realtime", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(30.dp))

        // O GRANDE BOTÃO PUSH-TO-TALK
        Box(
            modifier = Modifier
                .size(200.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            // 1. Ao Pressionar: Começar a Gravar
                            isRecording = true
                            logs = "A Gravar..."

                            audioManager.startRecording { base64Audio ->
                                // Envia pedaços de áudio em tempo real
                                val event = """
                                    {
                                        "type": "input_audio_buffer.append",
                                        "audio": "$base64Audio"
                                    }
                                """.trimIndent()
                                viewModel.sendRealtimeEvent(event)
                            }

                            tryAwaitRelease()

                            // 2. Ao Largar: Parar e Avisar a OpenAI
                            isRecording = false
                            audioManager.stopRecording()
                            logs = "A pensar..."

                            viewModel.sendRealtimeEvent(""" { "type": "input_audio_buffer.commit" } """)
                            viewModel.sendRealtimeEvent(""" { "type": "response.create" } """)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {}, // Gerido pelo pointerInput acima
                modifier = Modifier.fillMaxSize(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color.Red else Color.Blue
                )
            ) {
                Text(if (isRecording) "A FALAR..." else "SEGURA PARA FALAR")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("Status:", style = MaterialTheme.typography.titleMedium)
        Text(logs)
    }
}