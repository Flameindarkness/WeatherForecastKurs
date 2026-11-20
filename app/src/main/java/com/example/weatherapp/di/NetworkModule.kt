package com.example.weatherapp.di

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.remote.api.CityApiService
import com.example.weatherapp.data.remote.api.WeatherApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Модуль для предоставления зависимостей сетевого слоя
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    // Базовые URL для API
    private const val CITY_API_BASE_URL = "https://api.api-ninjas.com/v1/"
    private const val WEATHER_API_BASE_URL = "https://api.open-meteo.com/v1/"
    
    /**
     * Предоставляет Gson для сериализации/десериализации JSON
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * API-Ninjas требует ключ в заголовке X-Api-Key.
     * Ключ задаётся в local.properties: API_NINJAS_KEY=ваш_ключ
     */
    @Provides
    @Singleton
    @Named("CityApiClient")
    fun provideCityApiOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Интерцептор для добавления API ключа к каждому запросу к API-Ninjas
        val apiKeyInterceptor = okhttp3.Interceptor { chain ->
            val originalRequest = chain.request()
            val requestWithKey = originalRequest.newBuilder()
                .header("X-Api-Key", BuildConfig.API_NINJAS_KEY)
                .build()
            chain.proceed(requestWithKey)
        }

        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     *  City API (API-Ninjas)
     */
    @Provides
    @Singleton
    @Named("CityApi")
    fun provideCityApiRetrofit(
        @Named("CityApiClient") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CITY_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Weather API (Open-Meteo)
     */
    @Provides
    @Singleton
    @Named("WeatherApi")
    fun provideWeatherApiRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WEATHER_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * CityApiService
     */
    @Provides
    @Singleton
    fun provideCityApiService(
        @Named("CityApi") retrofit: Retrofit
    ): CityApiService {
        return retrofit.create(CityApiService::class.java)
    }
    
    /**
     *  WeatherApiService
     */
    @Provides
    @Singleton
    fun provideWeatherApiService(
        @Named("WeatherApi") retrofit: Retrofit
    ): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }
}

