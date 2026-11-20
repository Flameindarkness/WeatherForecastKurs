package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.repository.CityRepository

/**
 * Use case для получения координат города по названию
 * @param cityRepository Репозиторий для работы с городами
 */
class GetCityCoordinatesUseCase(
    private val cityRepository: CityRepository
) {
    /**
     * Выполнить получение координат города
     * @param cityName Название города
     * @return City объект с координатами или null, если город не найден
     */
    suspend operator fun invoke(cityName: String): City? {
        // Валидация входных данных
        if (cityName.isBlank()) {
            return null
        }
        
        return cityRepository.getCityCoordinates(cityName.trim())
    }
}

