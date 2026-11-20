package com.example.weatherapp.data.remote.api

import com.example.weatherapp.data.remote.dto.CityDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API сервис для работы с API-Ninjas
 * Используется для получения координат города по названию
 */
interface CityApiService {
    /**
     * Получить информацию о городе по названию
     * @param name Название города
     * @return Список городов (обычно один элемент)
     */
    @GET("city")
    suspend fun getCity(
        @Query("name") name: String
    ): List<CityDto>
}

