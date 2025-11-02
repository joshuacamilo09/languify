package com.languify.ui.theme

// Import for colors
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

// Base colors used throughout the app
val Primary = Color(0xFF5C6BC0)      // Main app color (blue)
val Secondary = Color(0xFF8E24AA)    // Accent color (purple)

// Background colors for light and dark modes
val LightBackground = Color(0xFFF5F7FA)
val LightOnBackground = Color(0xFF212121)

val DarkBackground = Color(0xFF121212)
val DarkOnBackground = Color(0xFFF5F5F5)

// Full color palette for Light Mode
val LightColors = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = LightBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = LightOnBackground,
    onSurface = LightOnBackground,
)

// Full color palette for Dark Mode
val DarkColors = darkColorScheme(
    primary = Primary.copy(alpha = 0.9f),
    secondary = Secondary.copy(alpha = 0.9f),
    background = DarkBackground,
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkOnBackground,
    onSurface = DarkOnBackground,
)
