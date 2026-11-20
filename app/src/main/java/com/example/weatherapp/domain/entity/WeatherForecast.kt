package com.example.weatherapp.domain.entity

/**
 * Сущность прогноза погоды
 * @param city Город, для которого составлен прогноз
 * @param hourlyForecast Список почасовых прогнозов
 */
data class WeatherForecast(
    val city: City,
    val hourlyForecast: List<HourlyWeather>
)
