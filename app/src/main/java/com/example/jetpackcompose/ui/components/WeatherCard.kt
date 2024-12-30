package com.example.jetpackcompose.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jetpackcompose.data.ForecastItem
import com.example.jetpackcompose.ui.views.convertUnixToTime

@Composable
fun WeatherCard(forecastItem: ForecastItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // Fill the available width
            .padding(vertical = 8.dp) // Vertical padding
            .padding(horizontal = 16.dp) // Horizontal padding
            .background(color = Color(0xFFBBDEFB), shape = RoundedCornerShape(16.dp)) // Background with rounded corners
            .padding(16.dp) // Inner padding
            .clip(RoundedCornerShape(16.dp)), // Clip content with rounded corners
        verticalAlignment = Alignment.CenterVertically // Vertically center the content
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${forecastItem.weather.firstOrNull()?.icon}@2x.png"), // Load weather icon
            contentDescription = null, // No content description
            modifier = Modifier.size(100.dp), // Set image size
            contentScale = ContentScale.Crop // Crop to fit the image
        )

        Spacer(modifier = Modifier.width(24.dp)) // Spacer between image and text

        Column(
            verticalArrangement = Arrangement.Center, // Center text vertically
            horizontalAlignment = Alignment.Start // Align text to the start (left)
        ) {
            Text(
                text = convertUnixToTime(forecastItem.dt), // Convert and display the time
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp), // Text style
                modifier = Modifier.padding(bottom = 4.dp) // Padding below the text
            )
            Text(
                text = "${forecastItem.main.temp}°C - ${forecastItem.weather.firstOrNull()?.description ?: "N/A"}", // Temperature and description
                color = Color.Gray, // Text color
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp) // Text style
            )
        }
    }
}