package com.example.weatherapp.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.presentation.ui.component.ErrorCard
import com.example.weatherapp.presentation.ui.component.WeatherForecastCard
import com.example.weatherapp.presentation.ui.state.WeatherUiState
import com.example.weatherapp.presentation.viewmodel.WeatherViewModel

/**
 * Главный экран приложения с прогнозом погоды
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cities by viewModel.cities.collectAsState()
    val forecasts by viewModel.forecasts.collectAsState()
    
    var cityInput by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Прогноз погоды") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (cities.isNotEmpty()) {
                        IconButton(onClick = { viewModel.refreshAll() }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Обновить прогноз",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Поле ввода города
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    label = { Text("Название города") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                Button(
                    onClick = {
                        if (cityInput.isNotBlank()) {
                            viewModel.addCity(cityInput)
                            cityInput = ""
                        }
                    }
                ) {
                    Text("Добавить")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Ошибку показываем над списком, чтобы уже добавленные города не пропадали
            val state = uiState
            if (state is WeatherUiState.Error) {
                ErrorCard(
                    message = state.message,
                    onRetry = { viewModel.refreshAll() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            when {
                state is WeatherUiState.Loading && forecasts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                forecasts.isEmpty() -> {
                    if (state !is WeatherUiState.Error) {
                        Text(
                            text = "Добавьте город для просмотра прогноза погоды",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }

                else -> {
                    if (state is WeatherUiState.Loading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(forecasts) { forecast ->
                            WeatherForecastCard(
                                forecast = forecast,
                                onRemove = { viewModel.removeCity(forecast) }
                            )
                        }
                    }
                }
            }
        }
    }
}

