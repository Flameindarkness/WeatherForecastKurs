package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.entity.City

/**
 * Репозиторий для работы с городами
 * Отвечает за получение координат по названию города
 */
interface CityRepository {
    /**
     * Получить координаты города по его названию
     * @param cityName Название города
     * @return City объект с координатами или null, если город не найден
     */
    suspend fun getCityCoordinates(cityName: String): City?
}

