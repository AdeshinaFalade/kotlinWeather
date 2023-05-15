package com.example.weatherapp

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class RemoteWeatherDataSource @Inject constructor(
    private val weatherService: WeatherService
): WeatherDataSource {
    override fun getWeather(place: String, apiKey: String, unit: String): Flow<Result<WeatherResponse>> {
        return apiFlow { weatherService.getWeather(place, apiKey, unit) }
    }

}