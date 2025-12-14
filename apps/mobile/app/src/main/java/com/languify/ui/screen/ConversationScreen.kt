package com.languify.ui.screen

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.languify.conversation.domain.model.ConversationMessage
import com.languify.conversation.domain.model.ConversationState
import com.languify.conversation.domain.viewmodel.ConversationViewModel
import com.languify.ui.theme.ClaudeAccent
import com.languify.ui.theme.ClaudeBorder
import com.languify.ui.theme.ClaudeTan
import com.languify.ui.theme.ClaudeTextSecondary

@Composable
fun ConversationScreen(
    onClose: () -> Unit,
    viewModel: ConversationViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val detectedLanguage by viewModel.detectedLanguage.collectAsState()
    val context = LocalContext.current

    val hasAudioPermission =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                viewModel.startConversation(autoStartRecording = true)
            } else {
                Toast.makeText(context, "Audio permission is required", Toast.LENGTH_LONG).show()
                onClose()
            }
        }

    LaunchedEffect(Unit) {
        if (hasAudioPermission) {
            viewModel.startConversation(autoStartRecording = true)
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(state) {
        if (state is ConversationState.Error) {
            val errorMessage = (state as ConversationState.Error).message
            Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MinimalHeader(
                detectedLanguage = detectedLanguage,
                onClose = {
                    viewModel.closeConversation()
                    onClose()
                },
            )

            AnimatedVisibility(
                visible = state !is ConversationState.Idle,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(tween(300)),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(tween(200)),
            ) {
                StateIndicator(state)
            }

            MessageList(
                messages = messages,
                modifier = Modifier.weight(1f),
            )

            ConversationControls(
                state = state,
                onStartRecording = { viewModel.startRecording() },
                onStopRecording = { viewModel.stopRecording() },
            )
        }
    }
}

@Composable
fun MinimalHeader(
    detectedLanguage: String?,
    onClose: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        AnimatedVisibility(
            visible = detectedLanguage != null,
            enter = fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)),
            exit = fadeOut(animationSpec = tween(200)),
        ) {
            Text(
                text = detectedLanguage ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = ClaudeTextSecondary,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
fun StateIndicator(state: ConversationState) {
    val text =
        when (state) {
            is ConversationState.Idle -> "Tap to start"
            is ConversationState.Initializing -> "Initializing..."
            is ConversationState.AwaitingSourceLanguage -> "Speak in your language"
            is ConversationState.LanguageDetected -> "Detected: ${state.language}"
            is ConversationState.Recording -> "Listening..."
            is ConversationState.Processing -> "Processing..."
            is ConversationState.Playing -> "Playing..."
            is ConversationState.Error -> "Error: ${state.message}"
        }

    val backgroundColor =
        when (state) {
            is ConversationState.Recording -> ClaudeAccent.copy(alpha = 0.08f)
            is ConversationState.Processing -> ClaudeTan.copy(alpha = 0.4f)
            is ConversationState.Playing -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            is ConversationState.Error -> Color(0xFFF44336).copy(alpha = 0.08f)
            else -> ClaudeTan.copy(alpha = 0.3f)
        }

    val textColor =
        when (state) {
            is ConversationState.Recording -> ClaudeAccent
            is ConversationState.Error -> Color(0xFFF44336)
            else -> ClaudeTextSecondary
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun MessageList(
    messages: List<ConversationMessage>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(
                index = messages.size - 1,
                scrollOffset = 0,
            )
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(messages, key = { it.hashCode() }) { message ->
            MessageBubble(message)
        }
    }
}

@Composable
fun MessageBubble(message: ConversationMessage) {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "messageAlpha",
    )

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "messageScale",
    )

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .alpha(alpha)
                .scale(scale),
        horizontalAlignment = if (message.fromMe) Alignment.End else Alignment.Start,
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color =
                if (message.fromMe) {
                    ClaudeTan
                } else {
                    MaterialTheme.colorScheme.surface
                },
            shadowElevation = 0.dp,
            tonalElevation = if (message.fromMe) 0.dp else 1.dp,
            modifier = Modifier.widthIn(max = 280.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = message.transcription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                if (message.transcription != message.translation) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = ClaudeBorder,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = message.translation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ClaudeTextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationControls(
    state: ConversationState,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
) {
    val recording =
        state is ConversationState.Recording ||
            state is ConversationState.AwaitingSourceLanguage ||
            state is ConversationState.LanguageDetected

    val processing = state is ConversationState.Processing || state is ConversationState.Initializing
    val playing = state is ConversationState.Playing
    val buttonSize by animateDpAsState(
        targetValue = if (recording) 76.dp else 68.dp,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
        label = "buttonSize",
    )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 24.dp),
        ) {
            FilledIconButton(
                onClick = {
                    if (processing || playing) return@FilledIconButton
                    if (recording) onStopRecording() else onStartRecording()
                },
                colors =
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor =
                            if (recording) {
                                Color(0xFFD32F2F)
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                        disabledContainerColor = ClaudeTan,
                    ),
                shape = CircleShape,
                modifier = Modifier.size(buttonSize),
                enabled = !processing && !playing,
            ) {
                if (processing) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp,
                    )
                } else {
                    Icon(
                        imageVector = if (recording) Icons.Default.Close else Icons.Default.Mic,
                        contentDescription = if (recording) "Stop" else "Record",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }
    }
}
