package com.example.weatherapp.data.repository

import com.example.weatherapp.data.mapper.WeatherForecastMapper
import com.example.weatherapp.data.remote.api.WeatherApiService
import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.entity.WeatherForecast
import com.example.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

/**
 * Реализация репозитория для работы с прогнозом погоды
 * @param weatherApiService API сервис для получения прогноза погоды
 */
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherRepository {
    
    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        forecastDays: Int
    ): Result<WeatherForecast> {
        return try {
            // Выполняем запрос к API
            val dto = weatherApiService.getWeatherForecast(
                latitude = latitude,
                longitude = longitude,
                forecastDays = forecastDays
            )
            
            // Создаем объект City из координат
            val city = City(
                name = "", // Имя будет установлено позже
                latitude = dto.latitude,
                longitude = dto.longitude
            )
            
            // Преобразуем DTO в domain entity
            val forecast = WeatherForecastMapper.toDomain(dto, city)
            
            Result.success(forecast)
        } catch (e: Exception) {
            // Возвращаем ошибку в Result
            Result.failure(e)
        }
    }
}

