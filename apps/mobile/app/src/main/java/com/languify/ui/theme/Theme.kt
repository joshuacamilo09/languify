package com.languify.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = ClaudeAccent,
        secondary = ClaudeAccentLight,
        tertiary = ClaudeDarkBeige,
        background = ClaudeDarkBackground,
        surface = ClaudeDarkSurface,
        onPrimary = ClaudeBackground,
        onSecondary = ClaudeBackground,
        onBackground = ClaudeBackground,
        onSurface = ClaudeBackground,
        outline = ClaudeDarkBorder,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = ClaudeAccent,
        secondary = ClaudeAccentLight,
        tertiary = ClaudeTan,
        background = ClaudeBackground,
        surface = ClaudeSurface,
        onPrimary = ClaudeBackground,
        onSecondary = ClaudeTextPrimary,
        onBackground = ClaudeTextPrimary,
        onSurface = ClaudeTextPrimary,
        outline = ClaudeBorder,
    )

@Composable
fun LanguifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disabled dynamic color for consistent minimal design
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
