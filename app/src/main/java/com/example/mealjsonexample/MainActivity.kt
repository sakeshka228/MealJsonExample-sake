package com.example.mealjsonexample

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navigationController = rememberNavController()
            val locationPickerViewModel: LocationPickerViewModel = viewModel()
            val locationViewModel: LocationViewModel = viewModel()
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) {
                Navigation(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    navigationController,
                    locationPickerViewModel,
                    locationViewModel
                )
            }
        }
    }
}


