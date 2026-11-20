package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.remote.dto.WeatherForecastDto
import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.entity.HourlyWeather
import com.example.weatherapp.domain.entity.WeatherForecast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Маппер для преобразования WeatherForecastDto в WeatherForecast entity
 */
object WeatherForecastMapper {
    // Формат даты и времени из API: "2024-01-15T12:00"
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Преобразовать WeatherForecastDto в WeatherForecast
     * @param dto DTO объект из API
     * @param city Город, для которого составлен прогноз
     * @return Domain entity объект
     */
    fun toDomain(dto: WeatherForecastDto, city: City): WeatherForecast {
        val hourlyData = dto.hourly
            ?: throw IllegalArgumentException("Данные о погоде отсутствуют")
        
        // Проверяем, что массивы времени и температуры имеют одинаковую длину
        if (hourlyData.time.size != hourlyData.temperature2m.size) {
            throw IllegalArgumentException("Несоответствие размеров массивов времени и температуры")
        }
        
        // Преобразуем списки времени и температуры в список HourlyWeather.
        // Часы без значения температуры (null) пропускаем
        val hourlyForecast = hourlyData.time.zip(hourlyData.temperature2m).mapNotNull { (timeStr, temp) ->
            if (temp == null) return@mapNotNull null
            try {
                val dateTime = LocalDateTime.parse(timeStr, dateTimeFormatter)
                HourlyWeather(
                    time = dateTime,
                    temperature = temp
                )
            } catch (e: Exception) {
                throw IllegalArgumentException("Ошибка парсинга времени: $timeStr", e)
            }
        }
        
        return WeatherForecast(
            city = city,
            hourlyForecast = hourlyForecast
        )
    }
}

