package com.example.weatherapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.entity.WeatherForecast
import com.example.weatherapp.domain.usecase.GetWeatherForCityUseCase
import com.example.weatherapp.presentation.ui.state.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления состоянием прогноза погоды
 * @param getWeatherForCityUseCase Use case для получения прогноза погоды по городу
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherForCityUseCase: GetWeatherForCityUseCase
) : ViewModel() {
    
    // Список городов для отслеживания прогноза
    private val _cities = MutableStateFlow<List<String>>(emptyList())
    val cities: StateFlow<List<String>> = _cities.asStateFlow()
    
    // UI состояние
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Initial)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    // Соответствие «введённое название -> название города из API».
    // Нужно, чтобы удалять город по карточке, где показано имя из API
    private val cityNameByQuery = mutableMapOf<String, String>()

    // Список прогнозов для каждого города
    private val _forecasts = MutableStateFlow<List<WeatherForecast>>(emptyList())
    val forecasts: StateFlow<List<WeatherForecast>> = _forecasts.asStateFlow()
    
    /**
     * Добавить город в список для отслеживания
     * @param cityName Название города
     */
    fun addCity(cityName: String) {
        val trimmedName = cityName.trim()
        
        // Валидация: проверяем, что название не пустое
        if (trimmedName.isBlank()) {
            _uiState.update { WeatherUiState.Error("Название города не может быть пустым") }
            return
        }
        
        // Проверяем, что город еще не добавлен (без учета регистра)
        val alreadyExists = _cities.value.any { it.equals(trimmedName, ignoreCase = true) }
        if (alreadyExists) {
            _uiState.update { WeatherUiState.Error("Город '$trimmedName' уже добавлен") }
            return
        }
        
        // Добавляем город в список
        _cities.update { it + trimmedName }
        
        // Загружаем прогноз для нового города
        loadWeatherForCity(trimmedName)
    }
    
    /**
     * Удалить город из списка
     * @param forecast Прогноз, карточку которого удаляет пользователь
     */
    fun removeCity(forecast: WeatherForecast) {
        val cityName = forecast.city.name
        val queries = cityNameByQuery.filterValues { it == cityName }.keys
        _cities.update { list -> list.filter { it !in queries } }
        queries.forEach { cityNameByQuery.remove(it) }
        _forecasts.update { list -> list.filter { it.city.name != cityName } }

        // Если список пуст, возвращаемся к начальному состоянию
        if (_cities.value.isEmpty()) {
            _uiState.update { WeatherUiState.Initial }
        }
    }

    /**
     * Загрузить прогноз погоды для города
     * @param cityName Название города
     */
    fun loadWeatherForCity(cityName: String) {
        viewModelScope.launch {
            // Устанавливаем состояние загрузки только если это первый запрос
            if (_forecasts.value.isEmpty()) {
                _uiState.update { WeatherUiState.Loading }
            }
            
            // Выполняем запрос через use case
            val result = getWeatherForCityUseCase(cityName)
            
            // Обрабатываем результат
            if (result.isSuccess) {
                val forecast = result.getOrThrow()
                cityNameByQuery[cityName] = forecast.city.name
                // Обновляем список прогнозов
                _forecasts.update { currentForecasts ->
                    // Удаляем старый прогноз для этого города, если он есть
                    val filtered = currentForecasts.filter { it.city.name != forecast.city.name }
                    filtered + forecast
                }
                
                // Обновляем UI состояние на успех
                _uiState.update { WeatherUiState.Success(_forecasts.value) }
            } else {
                val error = result.exceptionOrNull() ?: Exception("Неизвестная ошибка")
                // Обновляем UI состояние на ошибку
                _uiState.update { 
                    WeatherUiState.Error(error.message ?: "Неизвестная ошибка")
                }
                
                // Новый город, для которого не удалось получить прогноз, убираем из списка.
                // Уже загруженный город оставляем: при обновлении ошибка может быть временной
                if (cityName !in cityNameByQuery) {
                    _cities.update { list -> list.filter { it != cityName } }
                }
            }
        }
    }
    
    /**
     * Обновить прогнозы для всех городов
     */
    fun refreshAll() {
        val citiesToRefresh = _cities.value.toList()
        if (citiesToRefresh.isEmpty()) {
            return
        }
        
        _uiState.update { WeatherUiState.Loading }
        
        citiesToRefresh.forEach { cityName ->
            loadWeatherForCity(cityName)
        }
    }
}

