package com.languify.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.preference.PreferenceManager
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. Configuração Obrigatória do OSM (User Agent)
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    // Estado da Localização (Default: Lisboa)
    var userLocation by remember { mutableStateOf(GeoPoint(38.7169, -9.1399)) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    // Referência para o Mapa (para controlar zoom e centro programaticamente)
    var mapView by remember { mutableStateOf<MapView?>(null) }

    // Launcher de Permissões
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    // Verificar Permissões no Arranque
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hasLocationPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // 2. Lógica de Localização SEM Google (Usa LocationManager nativo)
    DisposableEffect(hasLocationPermission) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Atualiza o estado quando o utilizador se move
                userLocation = GeoPoint(location.latitude, location.longitude)

                // Opcional: Centrar o mapa automaticamente se quiseres "Seguir" o utilizador
                // mapView?.controller?.animateTo(userLocation)
            }
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
        }

        if (hasLocationPermission) {
            try {
                // Tenta obter a última localização conhecida imediatamente
                val lastKnownGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val lastKnownNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val bestLocation = lastKnownGPS ?: lastKnownNet

                if (bestLocation != null) {
                    userLocation = GeoPoint(bestLocation.latitude, bestLocation.longitude)
                    // Move a câmara logo no início
                    mapView?.controller?.setCenter(userLocation)
                    mapView?.controller?.setZoom(15.0)
                }

                // Pede atualizações constantes (substituto do requestLocationUpdates do Google)
                // MinTime: 5000ms (5 segundos), MinDistance: 10 metros
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10f, locationListener)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10f, locationListener)

            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

        onDispose {
            // Pára de usar o GPS quando saímos do ecrã para poupar bateria
            if (hasLocationPermission) {
                try {
                    locationManager.removeUpdates(locationListener)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // 3. Gestão do Ciclo de Vida do OSM (Importante para não bloquear a memória)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
        ) {
            // 4. O Mapa OSM
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK) // Estilo visual do mapa
                        setMultiTouchControls(true) // Zoom com dois dedos
                        controller.setZoom(15.0)
                        controller.setCenter(userLocation)
                        mapView = this // Guarda a referência
                    }
                },
                update = { view ->
                    // Atualiza o mapa quando o Compose redesenha
                    // Limpa overlays antigos (marcadores) para não duplicar
                    view.overlays.clear()

                    // Cria o Marcador "You are here"
                    val marker = Marker(view)
                    marker.position = userLocation
                    marker.title = "You are here"
                    marker.snippet = "Welcome to Languify"
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                    // Adiciona o marcador ao mapa
                    view.overlays.add(marker)

                    // Força o redesenho
                    view.invalidate()
                }
            )

            // Botão flutuante “Centrar em mim”
            FloatingActionButton(
                onClick = {
                    mapView?.controller?.animateTo(userLocation)
                    mapView?.controller?.setZoom(18.0) // Zoom mais perto ao clicar
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            ) {
                Icon(Icons.Filled.MyLocation, contentDescription = "Center on me")
            }
        }
    }
}