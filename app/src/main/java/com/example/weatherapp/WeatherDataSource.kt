package com.example.weatherapp

import kotlinx.coroutines.flow.Flow

interface WeatherDataSource {
    fun getWeather(place: String, apiKey: String, unit: String): Flow<Result<WeatherResponse>>
}