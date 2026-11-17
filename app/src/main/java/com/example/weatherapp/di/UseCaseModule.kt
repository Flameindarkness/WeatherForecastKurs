package com.example.weatherapp.di

import com.example.weatherapp.domain.repository.CityRepository
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.GetCityCoordinatesUseCase
import com.example.weatherapp.domain.usecase.GetWeatherForecastUseCase
import com.example.weatherapp.domain.usecase.GetWeatherForCityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль для предоставления Use Cases
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    /**
     *  GetCityCoordinatesUseCase
     */
    @Provides
    @Singleton
    fun provideGetCityCoordinatesUseCase(
        cityRepository: CityRepository
    ): GetCityCoordinatesUseCase {
        return GetCityCoordinatesUseCase(cityRepository)
    }
    
    /**
     *  GetWeatherForecastUseCase
     */
    @Provides
    @Singleton
    fun provideGetWeatherForecastUseCase(
        weatherRepository: WeatherRepository
    ): GetWeatherForecastUseCase {
        return GetWeatherForecastUseCase(weatherRepository)
    }
    
    /**
     *  GetWeatherForCityUseCase
     */
    @Provides
    @Singleton
    fun provideGetWeatherForCityUseCase(
        getCityCoordinatesUseCase: GetCityCoordinatesUseCase,
        getWeatherForecastUseCase: GetWeatherForecastUseCase
    ): GetWeatherForCityUseCase {
        return GetWeatherForCityUseCase(
            getCityCoordinatesUseCase,
            getWeatherForecastUseCase
        )
    }
}

