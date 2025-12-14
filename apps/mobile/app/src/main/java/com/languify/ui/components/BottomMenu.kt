package com.languify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.languify.ui.theme.ClaudeAccent
import com.languify.ui.theme.ClaudeTan

@Composable
fun BottomMenu(
    onConversationClick: () -> Unit = {},
    onMicrophoneClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
        tonalElevation = 2.dp,
        modifier = modifier.padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            MinimalIconButton(
                onClick = onConversationClick,
                icon = Icons.AutoMirrored.Filled.Chat,
                contentDescription = "Conversation",
            )

            MinimalIconButton(
                onClick = onMicrophoneClick,
                icon = Icons.Default.Mic,
                contentDescription = "Microphone",
                isPrimary = true,
            )

            MinimalIconButton(
                onClick = onProfileClick,
                icon = Icons.Default.AccountCircle,
                contentDescription = "Profile",
            )
        }
    }
}

@Composable
private fun MinimalIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isPrimary: Boolean = false,
) {
    val buttonSize = if (isPrimary) 60.dp else 52.dp

    FilledIconButton(
        onClick = onClick,
        colors =
            IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isPrimary) ClaudeAccent else ClaudeTan,
            ),
        shape = CircleShape,
        modifier = Modifier.size(buttonSize),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isPrimary) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(if (isPrimary) 28.dp else 24.dp),
        )
    }
}
