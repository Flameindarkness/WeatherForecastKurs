package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.entity.WeatherForecast

/**
 * Репозиторий для работы с прогнозом погоды
 * Отвечает за получение прогноза погоды для города
 */
interface WeatherRepository {
    /**
     * Получить прогноз погоды для города по координатам
     * @param latitude Широта
     * @param longitude Долгота
     * @param forecastDays Количество дней прогноза (по умолчанию 16)
     * @return WeatherForecast объект с прогнозом погоды
     */
    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        forecastDays: Int = 16
    ): Result<WeatherForecast>
}

