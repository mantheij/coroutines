package com.example.jetpackcompose

// MainActivity serves as the entry point for the app.
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcompose.viewmodel.WeatherViewModel
import com.example.jetpackcompose.ui.WeatherApp
import com.example.jetpackcompose.viewmodel.PopupServiceManager

class MainActivity : ComponentActivity() {

    // Manager to handle pop-up services for the app
    private val popupServiceManager = PopupServiceManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle pop-up service permissions and setup
        handlePopupService()

        // Set the app's UI content using Jetpack Compose
        setContent {
            val viewModel: WeatherViewModel = viewModel() // Retrieve the ViewModel for weather data
            WeatherApp(viewModel) // Launch the weather application UI
        }
    }

    // Handles permissions or service start based on the Android version
    private fun handlePopupService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            popupServiceManager.requestPermission() // Request permissions for newer Android versions
        } else {
            popupServiceManager.startPopupService() // Start the pop-up service for older Android versions
        }
    }
}