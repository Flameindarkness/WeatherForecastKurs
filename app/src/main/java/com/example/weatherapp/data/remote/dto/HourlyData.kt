package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Почасовые данные прогноза
 */
data class HourlyData(
    @SerializedName("time")
    val time: List<String>,
    
    // Для часов, на которые у модели нет данных, Open-Meteo возвращает null
    @SerializedName("temperature_2m")
    val temperature2m: List<Double?>
)
