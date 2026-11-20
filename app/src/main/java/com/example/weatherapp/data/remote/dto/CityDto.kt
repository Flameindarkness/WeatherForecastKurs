package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO для ответа API-Ninjas при поиске города
 */
data class CityDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double,
    
    @SerializedName("country")
    val country: String? = null
)

