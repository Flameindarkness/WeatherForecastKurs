package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.remote.dto.CityDto
import com.example.weatherapp.domain.entity.City

/**
 * Маппер для преобразования CityDto в City entity
 */
object CityMapper {
    /**
     * Преобразовать CityDto в City
     * @param dto DTO объект из API
     * @return Domain entity объект
     */
    fun toDomain(dto: CityDto): City {
        return City(
            name = dto.name,
            latitude = dto.latitude,
            longitude = dto.longitude
        )
    }
}

