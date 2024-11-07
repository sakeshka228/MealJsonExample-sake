package com.example.mealjsonexample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.navigation.NavController

@Composable
fun ProductListScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Список продуктов", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = { navController.navigate("location_picker") }) {
            Text("Выбрать место покупки")
        }
        // Здесь можно добавить отображение списка продуктов
    }
}

