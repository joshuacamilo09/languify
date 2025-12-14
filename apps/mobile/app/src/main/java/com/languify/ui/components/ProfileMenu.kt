package com.languify.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun ProfileMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = DpOffset(x = (-40).dp, y = (-70).dp),
    ) {
        DropdownMenuItem(
            text = { Text("Sign Out") },
            onClick = {
                onSignOut()
                onDismiss()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Sign Out",
                )
            },
        )
    }
}
