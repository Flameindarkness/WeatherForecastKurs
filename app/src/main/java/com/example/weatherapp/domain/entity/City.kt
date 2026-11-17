package com.example.weatherapp.domain.entity

/**
 * Сущность города с координатами
 * @param name Название города
 * @param latitude Широта
 * @param longitude Долгота
 */
data class City (
    val name: String,
    val latitude: Double,
    val longitude: Double
)

