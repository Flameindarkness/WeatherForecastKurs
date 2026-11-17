package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Почасовые данные прогноза
 */
data class HourlyData(
    @SerializedName("time")
    val time: List<String>,
    
    @SerializedName("temperature_2m")
    val temperature2m: List<Double>
)
