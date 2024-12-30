package com.example.jetpackcompose.ui

// Main UI composable for the Weather App
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.jetpackcompose.viewmodel.WeatherViewModel
import com.example.jetpackcompose.ui.components.BottomNavBar
import com.example.jetpackcompose.ui.views.CurrentWeatherView
import com.example.jetpackcompose.ui.views.ForecastWeatherView
import com.example.jetpackcompose.ui.views.SettingsView

@Composable
fun WeatherApp(viewModel: WeatherViewModel) {
    // State flows for current weather, forecast, and icon URL
    val currentWeather by viewModel.currentWeather.collectAsState()
    val forecast by viewModel.forecast.collectAsState()
    val iconUrl by viewModel.iconUrl.collectAsState()

    // Track the selected navigation item
    var selectedItem by remember { mutableStateOf(0) }

    // Define background colors for upper and lower halves
    val upperHalfColor = Color.White
    val lowerHalfColor = Color(0xFF1E88E5)

    // Main container for the app layout
    Box(modifier = Modifier.fillMaxSize()) {
        // Background split into two halves
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(upperHalfColor) // Upper half background
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(lowerHalfColor) // Lower half background
            )
        }

        // Foreground container for content and navigation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .background(upperHalfColor)
        ) {
            // Main content column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Add spacing at the top

                // Show content based on selected navigation item
                when (selectedItem) {
                    0 -> CurrentWeatherView(currentWeather = currentWeather, iconUrl = iconUrl)
                    1 -> ForecastWeatherView(forecast = forecast)
                    2 -> SettingsView(onSave = { selectedItem = 0 }) // Navigate back to current weather
                }
            }

            // Bottom navigation bar
            BottomNavBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }, // Update selected item
                modifier = Modifier.align(Alignment.BottomCenter),
                backgroundColor = lowerHalfColor // Set background color
            )
        }
    }
}