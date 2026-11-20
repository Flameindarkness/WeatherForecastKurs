package com.example.weatherapp.presentation.ui.state

import com.example.weatherapp.domain.entity.WeatherForecast

/**
 * UI состояние для экрана прогноза погоды
 */
sealed class WeatherUiState {
    /**
     * Начальное состояние (пустое)
     */
    data object Initial : WeatherUiState()
    
    /**
     * Состояние загрузки
     */
    data object Loading : WeatherUiState()
    
    /**
     * Состояние успешной загрузки с данными
     */
    data class Success(val forecasts: List<WeatherForecast>) : WeatherUiState()
    
    /**
     * Состояние ошибки
     */
    data class Error(val message: String) : WeatherUiState()
}

