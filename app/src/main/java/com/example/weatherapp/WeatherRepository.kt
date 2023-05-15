package com.example.weatherapp

import javax.inject.Inject
import com.example.weatherapp.BuildConfig


class WeatherRepository @Inject constructor(
    private val remoteWeatherDataSource: RemoteWeatherDataSource
) {

    fun getWeather(place: String) =
        remoteWeatherDataSource.getWeather(
            place = place,
            apiKey = BuildConfig.APIKEY,
            unit = "metric"
        )
}