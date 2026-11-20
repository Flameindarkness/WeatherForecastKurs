package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO для ответа Open-Meteo API при получении прогноза погоды
 */
data class WeatherForecastDto(
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double,
    
    @SerializedName("hourly")
    val hourly: HourlyData? = null
)
