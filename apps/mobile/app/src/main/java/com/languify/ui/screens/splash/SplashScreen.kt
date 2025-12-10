package com.languify.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.languify.R
import com.languify.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay

/**
 * SplashScreen animada — verifica se o utilizador está logado e navega
 * para Home ou Login automaticamente, com animação de fade-in suave.
 */
@Composable
fun SplashScreen(navController: NavController, profileViewModel: ProfileViewModel) {

    // 🔹 Estado para controlar visibilidade do fade-in
    var visible by remember { mutableStateOf(false) }

    // 🔹 Animação de transparência (alpha)
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1200), // duração mais suave
        label = "fade-in"
    )

    // 🔹 Efeito colateral — inicia animação e navegação
    LaunchedEffect(Unit) {
        visible = true
        delay(2000) // tempo total do splash
        val isLoggedIn = profileViewModel.isLoggedIn.value

        // ✅ Se logado → Home / Senão → Login
        if (isLoggedIn) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true } // remove splash da pilha
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // 🔹 Interface da Splash
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

                    // 🟣 Logotipo (usa o teu logo principal)
                    Image(
                        painter = painterResource(id = R.drawable.languify_logo), // substitui pelo teu ficheiro real
                        contentDescription = "App logo",
                        modifier = Modifier
                            .size(140.dp)
                            .alpha(alpha)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🟣 Nome da app
                    Text(
                        text = "Languify",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.alpha(alpha)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Splash Screen")
@Composable
fun SplashScreenPreview() {
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.languify_logo),
                    contentDescription = "App logo",
                    modifier = Modifier.size(140.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Languify",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
