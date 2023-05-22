package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.ui.theme.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherVM @Inject constructor(
    private val weatherRepository: WeatherRepository
): BaseViewModel() {
    val uiState = MutableStateFlow(WeatherState())

    fun getWeather() {
        uiState.update {
            it.copy(isLoading = true)
        }
        launchSuspend {
            weatherRepository.getWeather(uiState.value.city.trim())
                .catch { e ->
                    uiState.value = uiState.value.copy(
                        error = e.message ?: "An error occurred"
                    )
                }
                .collect { result ->
                    when (result){
                        is Result.Success -> {
                            uiState.value = uiState.value.copy(
                                temperature = result.data.main.temp.toString(),
                                place = result.data.name,
                                country = result.data.sys.country,
                                description = result.data.weather.first().description,
                                isLoading = false,
                                icon = result.data.weather.first().icon,
                                imageUrl = "${BuildConfig.IMAGEHOST}${result.data.weather.first().icon}.png"
                            )
                        }
                        is Result.Error -> {
                            uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
        }


    }

    override fun handleException(throwable: Throwable) {
        Log.d("coroutine", throwable.message ?: "Error occurred! Please try again.")
        uiState.value = uiState.value.copy(
            error = throwable.message ?: "An error occurred",
            isLoading = false
        )
    }

    fun updateCity(city: String){
        uiState.update { it.copy(city = city) }
    }

    fun clearError(){
        uiState.update { it.copy(
            error = null
        ) }
    }
}