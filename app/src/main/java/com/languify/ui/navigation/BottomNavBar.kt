package com.languify.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.languify.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.History // <-- Add this import

/**
 * The bottom navigation bar visible on all screens.
 */
@Composable
fun BottomNavBar(navController: NavController) {
    // Define all the tabs and their routes/icons
    val items = listOf(
        BottomNavItem("home", R.string.tab_home, Icons.Filled.Home),
        BottomNavItem("history", R.string.tab_history, Icons.Filled.History),
        BottomNavItem("map", R.string.tab_map, Icons.Filled.Place),
        BottomNavItem("profile", R.string.tab_profile, Icons.Filled.Person),
    )

    // Observe the current screen to highlight the active tab
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                // Highlight if the current screen is active
                selected = currentRoute == item.route,

                // Navigate when user taps a tab
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Prevent back stack overflow
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },

                // Set the icon and label
                icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                label = { Text(stringResource(item.labelRes)) }
            )
        }
    }
}
