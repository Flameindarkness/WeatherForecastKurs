package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.entity.WeatherForecast
import com.example.weatherapp.domain.repository.WeatherRepository

/**
 * Use case для получения прогноза погоды
 * @param weatherRepository Репозиторий для работы с прогнозом погоды
 */
class GetWeatherForecastUseCase(
    private val weatherRepository: WeatherRepository
) {
    /**
     * Выполнить получение прогноза погоды
     * @param latitude Широта
     * @param longitude Долгота
     * @param forecastDays Количество дней прогноза (по умолчанию 16)
     * @return Result с прогнозом погоды или ошибкой
     */
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        forecastDays: Int = 16
    ): Result<WeatherForecast> {
        // Валидация координат
        if (latitude < -90 || latitude > 90) {
            return Result.failure(IllegalArgumentException("Широта должна быть в диапазоне от -90 до 90"))
        }
        if (longitude < -180 || longitude > 180) {
            return Result.failure(IllegalArgumentException("Долгота должна быть в диапазоне от -180 до 180"))
        }
        if (forecastDays < 1 || forecastDays > 16) {
            return Result.failure(IllegalArgumentException("Количество дней прогноза должно быть от 1 до 16"))
        }

        // Вызываем метод репозитория через инстанс, внедрённый в use case
        return weatherRepository.getWeatherForecast(latitude, longitude, forecastDays)
    }
}

