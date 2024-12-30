package com.example.jetpackcompose.api

// Service for interacting with the OpenWeatherMap API
import android.util.Log
import com.example.jetpackcompose.data.ForecastData
import com.example.jetpackcompose.data.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object WeatherApiService {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // OkHttp client for HTTP requests
    private val client = OkHttpClient.Builder().build()

    // Retrofit instance for API calls
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create()) // JSON serialization
        .build()

    // API interface for defining endpoints
    private val api = retrofit.create(WeatherApi::class.java)

    interface WeatherApi {
        // Fetch current weather for a city
        @GET("weather")
        suspend fun fetchWeather(
            @Query("q") city: String, // City name
            @Query("appid") apiKey: String, // API key
            @Query("units") units: String = "metric" // Units for temperature
        ): retrofit2.Response<WeatherData>

        // Fetch weather forecast for a city
        @GET("forecast")
        suspend fun fetchForecast(
            @Query("q") city: String, // City name
            @Query("appid") apiKey: String, // API key
            @Query("units") units: String = "metric" // Units for temperature
        ): retrofit2.Response<ForecastData>
    }

    // Fetch current weather data
    suspend fun fetchWeather(city: String, apiKey: String): WeatherData? {
        return try {
            withContext(Dispatchers.Default) {
                val response = api.fetchWeather(city, apiKey)
                if (response.isSuccessful) {
                    response.body() // Return weather data if successful
                } else {
                    Log.e("WeatherApiService", "Failed to fetch data: ${response.code()}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherApiService", "Error fetching data: ${e.message}")
            null
        }
    }

    // Fetch weather forecast data
    suspend fun fetchForecast(city: String, apiKey: String): ForecastData? {
        return try {
            withContext(Dispatchers.Default) {
                val response = api.fetchForecast(city, apiKey)
                if (response.isSuccessful) {
                    response.body() // Return forecast data if successful
                } else {
                    Log.e("WeatherApiService", "Failed to fetch forecast: ${response.code()} - ${response.message()}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherApiService", "Error fetching forecast: ${e.message}")
            null
        }
    }
}