package com.languify.ui.screens.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding  // <-- ADICIONA ESTA LINHA
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


/**
 * Map screen displaying chat locations on Google Maps.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Map") }) }
    ) { padding ->
        val start = LatLng(0.0, 0.0)

        // Initialize the camera state (starting position and zoom)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(start, 2f)
        }

        // Display the map
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            cameraPositionState = cameraPositionState
        ) {
            // Example pin (Lisbon)
            Marker(
                state = MarkerState(position = LatLng(38.7223, -9.1393)),
                title = "Lisboa"
            )
        }
    }
}
