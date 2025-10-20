package com.languify.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.languify.R
import kotlinx.coroutines.delay
import androidx.compose.material3.Text

/**
 * Simple animated splash screen that fades in and out before entering the app.
 */
@Composable
fun SplashScreen(navController: NavController) {
    // Controls visibility for fade-in animation
    var visible by remember { mutableStateOf(false) }

    // Alpha animation (transparency)
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1500), label = ""
    )

    // When the screen launches, trigger animation and navigate after delay
    LaunchedEffect(Unit) {
        visible = true
        delay(2500) // total time splash stays visible
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true } // remove splash from backstack
        }
    }

    // UI content
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = true) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // 🔹 Logo (replace with your icon later)
                    Image(
                        painter = painterResource(id = R.drawable.languify_logo),
                        contentDescription = "App logo",
                        modifier = Modifier
                            .size(140.dp)
                            .alpha(alpha)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🔹 App name text
                    Text(
                        text = "Languify",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.alpha(alpha)
                    )
                }
            }
        }
    }
}
