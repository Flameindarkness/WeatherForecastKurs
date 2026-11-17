package com.example.weatherapp.domain.entity

import java.time.LocalDateTime

/**
 * Почасовой прогноз погоды
 * @param time Время прогноза
 * @param temperature Температура в градусах Цельсия
 */
data class HourlyWeather(
    val time: LocalDateTime,
    val temperature: Double
)
