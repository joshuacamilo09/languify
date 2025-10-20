package com.languify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * The root theme composable for the whole app.
 * It switches automatically between light and dark modes.
 */
@Composable
fun LanguifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // uses system preference by default
    content: @Composable () -> Unit
) {
    // Choose the appropriate color palette
    val colors = if (darkTheme) DarkColors else LightColors

    // Apply theme settings globally
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
