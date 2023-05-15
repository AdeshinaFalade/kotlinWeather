package com.example.weatherapp


data class WeatherState(
    val isLoading: Boolean = false,
    val description: String = "",
    val icon: String = "",
    val temperature: String = "",
    val place: String = "",
    val city: String = "",
    val country: String = "",
    val imageUrl: String = "",
    val error: String? = null,
)
