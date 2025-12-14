package com.languify.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.languify.identity.auth.domain.viewmodel.AuthViewModel
import com.languify.ui.components.BottomMenu
import com.languify.ui.components.ProfileMenu

@Composable
fun HomeScreen(authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasLocationPermission by remember { mutableStateOf(isLocationPermissionGranted(context)) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            hasLocationPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            fetchCurrentLocation(fusedLocationClient) { userLocation = it }
        }
    }

    val defaultLocation = LatLng(37.7749, -122.4194)
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
        }

    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    14f,
                ),
            )
        }
    }

    var onConversation by remember { mutableStateOf(false) }
    var showProfileMenu by remember { mutableStateOf(false) }

    if (onConversation) {
        ConversationScreen(onClose = { onConversation = false })
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                contentPadding = PaddingValues(start = 16.dp, bottom = 16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                userLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = location),
                        title = "You are here",
                    )
                }
            }

            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 48.dp, end = 32.dp),
            ) {
                ProfileMenu(
                    expanded = showProfileMenu,
                    onDismiss = { showProfileMenu = false },
                    onSignOut = { authViewModel.signOut() },
                )
            }

            BottomMenu(
                onConversationClick = { },
                onMicrophoneClick = { onConversation = true },
                onProfileClick = { showProfileMenu = true },
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp),
            )
        }
    }
}

private fun isLocationPermissionGranted(context: Context): Boolean {
    val hasFineLocation =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocation =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

    return hasFineLocation || hasCoarseLocation
}

private fun fetchCurrentLocation(
    fusedLocationProviderClient: FusedLocationProviderClient,
    onLocationFound: (LatLng) -> Unit,
) {
    fusedLocationProviderClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onLocationFound(LatLng(location.latitude, location.longitude))
            } else {
                fusedLocationProviderClient
                    .getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        CancellationTokenSource().token,
                    ).addOnSuccessListener { currentLocation ->
                        currentLocation?.let {
                            onLocationFound(LatLng(it.latitude, it.longitude))
                        }
                    }
            }
        }
}
