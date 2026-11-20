package com.example.weatherapp.presentation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.domain.entity.WeatherForecast
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Диапазон отображаемого прогноза
 */
private enum class ForecastRange(val title: String) {
    TODAY("Сегодня"),
    NEXT_24_HOURS("24 часа"),
    TOMORROW("Завтра"),
    NEXT_7_DAYS("7 дней")
}

/**
 * Карточка для отображения прогноза погоды для города
 * @param forecast Прогноз погоды
 * @param onRemove Callback для удаления города из списка
 */
@Composable
fun WeatherForecastCard(
    forecast: WeatherForecast,
    onRemove: () -> Unit
) {
    // Локальное состояние: выбранный диапазон прогноза
    var range by remember { mutableStateOf(ForecastRange.TODAY) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок с названием города и кнопкой удаления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = forecast.city.name.ifBlank { "Город" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Координаты: ${forecast.city.latitude}, ${forecast.city.longitude}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onRemove) {
                    Text("✕", style = MaterialTheme.typography.titleLarge)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Переключатели диапазона прогноза
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ForecastRange.values().forEach { option ->
                    FilterChip(
                        selected = range == option,
                        onClick = { range = option },
                        label = { Text(option.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (range) {
                ForecastRange.TODAY -> TodayForecastSection(forecast)
                ForecastRange.NEXT_24_HOURS -> Next24HoursSection(forecast)
                ForecastRange.TOMORROW -> TomorrowSection(forecast)
                ForecastRange.NEXT_7_DAYS -> Next7DaysSection(forecast)
            }
        }
    }
}

/**
 * Прогноз только на сегодня (по часам)
 */
@Composable
private fun TodayForecastSection(forecast: WeatherForecast) {
    val today: LocalDate = LocalDate.now()
    val todayHours = forecast.hourlyForecast.filter { it.time.toLocalDate() == today }

    Text(
        text = "Сегодня по часам:",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    todayHours
        .take(24)
        .chunked(4)
        .forEach { group ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                group.forEach { hourly ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = hourly.time.format(timeFormatter),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${hourly.temperature.toInt()}°C",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = temperatureColor(hourly.temperature)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

    if (todayHours.isEmpty()) {
        Text(
            text = "Данных на сегодня нет",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Прогноз на ближайшие 24 часа
 */
@Composable
private fun Next24HoursSection(forecast: WeatherForecast) {
    Text(
        text = "Прогноз на ближайшие 24 часа:",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(8.dp))

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val next24 = forecast.hourlyForecast
        .sortedBy { it.time }
        .take(24)

    next24.chunked(4).forEach { hourGroup ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            hourGroup.forEach { hourly ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = hourly.time.format(timeFormatter),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${hourly.temperature.toInt()}°C",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = temperatureColor(hourly.temperature)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * Прогноз по дням на 7 дней: минимальная/максимальная и средняя температура
 */
@Composable
private fun Next7DaysSection(forecast: WeatherForecast) {
    Text(
        text = "Прогноз по дням (7 дней):",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(8.dp))

    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM")

    val groupedByDay = forecast.hourlyForecast
        .sortedBy { it.time }
        .groupBy { it.time.toLocalDate() }
        .entries
        .sortedBy { it.key }
        .take(7)

    groupedByDay.forEach { (date, hours) ->
        val minTemp = hours.minOfOrNull { it.temperature } ?: return@forEach
        val maxTemp = hours.maxOfOrNull { it.temperature } ?: return@forEach
        val avgTemp = hours.map { it.temperature }.average()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date.format(dateFormatter),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "днём ~ ${avgTemp.toInt()}°C",
                    style = MaterialTheme.typography.bodyMedium,
                    color = temperatureColor(avgTemp)
                )
                Text(
                    text = "мин ${minTemp.toInt()}° / макс ${maxTemp.toInt()}°",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 4.dp))
    }
}

/**
 * Прогноз на завтра (по часам, как для «Сегодня»)
 */
@Composable
private fun TomorrowSection(forecast: WeatherForecast) {
    val tomorrow: LocalDate = LocalDate.now().plusDays(1)
    val tomorrowHours = forecast.hourlyForecast.filter { it.time.toLocalDate() == tomorrow }

    Text(
        text = "Завтра по часам:",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    tomorrowHours
        .take(24)
        .chunked(4)
        .forEach { group ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                group.forEach { hourly ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = hourly.time.format(timeFormatter),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${hourly.temperature.toInt()}°C",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = temperatureColor(hourly.temperature)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

    if (tomorrowHours.isEmpty()) {
        Text(
            text = "Данных на завтра нет",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Подбор цвета текста температуры в зависимости от значения.
 *
 * Очень холодно (< -10)  -> синий
 * Холодно (-10..0)       -> голубой
 * Тепло (0..20)          -> зеленоватый
 * Жарко (20..30)         -> оранжевый
 * Очень жарко (> 30)     -> красный
 */
@Composable
private fun temperatureColor(tempC: Double): androidx.compose.ui.graphics.Color {
    return when {
        tempC < -10 -> MaterialTheme.colorScheme.primary
        tempC < 0 -> MaterialTheme.colorScheme.secondary
        tempC < 20 -> MaterialTheme.colorScheme.tertiary
        tempC < 30 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.error
    }
}

