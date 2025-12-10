package com.languify.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.luminance
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Estado inicial — Lisboa
    var userLocation by remember { mutableStateOf(LatLng(38.7169, -9.1399)) }

    // Estado de permissões
    var hasLocationPermission by remember { mutableStateOf(false) }

    // Estado da câmera e do mapa
    val cameraPositionState = rememberCameraPositionState ()

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.latitude, it.longitude),
                    15f //nível de zoom (podes ajustar entre 10f e 18f)
                )
            )
        }
    }

    // UI settings e propriedades
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }

    // Modo escuro automático do mapa
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5
    val mapStyle = if (isDarkTheme) {
        // Tema escuro
        MapStyleOptions.loadRawResourceStyle(context, com.languify.R.raw.map_style_dark)
    } else {
        // Tema claro
        MapStyleOptions.loadRawResourceStyle(context, com.languify.R.raw.map_style_light)
    }

    var properties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapStyleOptions = mapStyle
            )
        )
    }

    // Launcher de permissão
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    // Solicita permissão ao entrar
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasLocationPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Atualiza a posição do utilizador
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(userLocation, 15f)
                        )
                    }
                }
            }
        }
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val scope = rememberCoroutineScope()

    //seguir o utilizador enquanto ele se move
    LaunchedEffect(Unit) {
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create().apply {
                interval = 5000 // atualiza a cada 5 segundos
                fastestInterval = 2000
                priority = Priority.PRIORITY_HIGH_ACCURACY
            },
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    scope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLng(
                                LatLng(location.latitude, location.longitude)
                            )
                        )
                    }
                }
            },
            Looper.getMainLooper()
        )
    }


    // UI
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Map") })
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 50.dp, top = 16.dp),
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                properties = properties
            ) {
                Marker(
                    state = MarkerState(position = userLocation),
                    title = "You are here",
                    snippet = "welcome to languify"
                )
            }

            // Botão flutuante “Centrar em mim”
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(userLocation, 15f)
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end =16.dp)
            ) {
                Icon(Icons.Filled.MyLocation, contentDescription = "Center on me")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Map Screen")
@Composable
fun MapScreenPreview() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Map") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Preview simplificado sem Google Maps
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Map View",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Google Maps will be displayed here",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            FloatingActionButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 16.dp)
            ) {
                Icon(Icons.Filled.MyLocation, contentDescription = "Center on me")
            }
        }
    }
}
