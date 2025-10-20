package com.languify.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a single item in the bottom navigation bar.
 */
data class BottomNavItem(
    val route: String,          // Navigation route (used by NavHost)
    @StringRes val labelRes: Int, // Label text (from strings.xml)
    val icon: ImageVector       // Icon displayed in the tab
)
