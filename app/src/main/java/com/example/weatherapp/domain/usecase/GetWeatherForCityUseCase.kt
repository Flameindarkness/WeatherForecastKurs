package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.entity.WeatherForecast

/**
 * Use case для получения прогноза погоды по названию города
 * Объединяет получение координат и прогноза погоды
 * @param getCityCoordinatesUseCase Use case для получения координат города
 * @param getWeatherForecastUseCase Use case для получения прогноза погоды
 */
class GetWeatherForCityUseCase(
    private val getCityCoordinatesUseCase: GetCityCoordinatesUseCase,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase
) {
    /**
     * Выполнить получение прогноза погоды для города
     * @param cityName Название города
     * @param forecastDays Количество дней прогноза (по умолчанию 16)
     * @return Result с прогнозом погоды или ошибкой
     */
    suspend operator fun invoke(
        cityName: String,
        forecastDays: Int = 16
    ): Result<WeatherForecast> {
        // Получаем координаты города
        val city = getCityCoordinatesUseCase(cityName)
            ?: return Result.failure(IllegalArgumentException("Город '$cityName' не найден"))
        
        // Получаем прогноз погоды по координатам
        return getWeatherForecastUseCase(city.latitude, city.longitude, forecastDays)
            .map { forecast ->
                // Обновляем город в прогнозе на тот, который был запрошен
                forecast.copy(city = city)
            }
    }
}

