package com.example.mealjsonexample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.compose.ui.platform.LocalContext

@SuppressLint("UnrememberedMutableState")
@Composable
fun LocationPickerScreen(navController: NavController, viewModel: LocationPickerViewModel ) {
    Text("Здесь будет карта и сохранённые координаты")
    val context = LocalContext.current
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val currentLocation = remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val savedLocations = viewModel.savedLocations.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        // Проверка разрешений
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation.value = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }


    // Получение текущей геолокации
    LaunchedEffect(Unit) {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }

    Column {
        GoogleMap(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            // Отображение маркера на текущей локации
            Marker(
                state = MarkerState(position = currentLocation.value)
            )
        }

        Button(
            onClick = { viewModel.saveLocation(currentLocation.value) },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
        ) {
            Text("Сохранить текущее местоположение")
        }

        LazyColumn {
            items(savedLocations.value) { location ->
                Text("Широта: ${location.latitude}, Долгота: ${location.longitude}")
            }
        }
    }
}

