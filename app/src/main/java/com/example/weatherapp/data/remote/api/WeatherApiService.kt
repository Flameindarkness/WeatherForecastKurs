package com.example.weatherapp.data.remote.api

import com.example.weatherapp.data.remote.dto.WeatherForecastDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API сервис для работы с Open-Meteo
 * Используется для получения прогноза погоды
 */
interface WeatherApiService {
    /**
     * Получить прогноз погоды по координатам
     * @param latitude Широта
     * @param longitude Долгота
     * @param hourly Параметры для почасового прогноза (temperature_2m)
     * @param forecastDays Количество дней прогноза (максимум 16)
     * @return WeatherForecastDto с прогнозом погоды
     */
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m",
        @Query("forecast_days") forecastDays: Int = 16
    ): WeatherForecastDto
}

