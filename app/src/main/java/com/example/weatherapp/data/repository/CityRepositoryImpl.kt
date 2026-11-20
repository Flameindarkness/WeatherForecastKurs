package com.example.weatherapp.data.repository

import com.example.weatherapp.data.mapper.CityMapper
import com.example.weatherapp.data.remote.api.CityApiService
import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.repository.CityRepository
import javax.inject.Inject

/**
 * Реализация репозитория для работы с городами
 * @param cityApiService API сервис для получения информации о городах
 */
class CityRepositoryImpl @Inject constructor(
    private val cityApiService: CityApiService
) : CityRepository {
    
    /**
     * Простейшее соответствие распространённых русских названий городов
     * их английским вариантам для API-Ninjas.
     *
     * Это нужно, чтобы пользователь мог вводить города по‑русски,
     * а запрос к API выполнялся с корректным английским названием.
     */
    private val russianToEnglishCityMap: Map<String, String> = mapOf(
        "москва" to "Moscow",
        "санкт-петербург" to "Saint Petersburg",
        "санкт петербург" to "Saint Petersburg",
        "питер" to "Saint Petersburg",
        "новосибирск" to "Novosibirsk",
        "екатеринбург" to "Yekaterinburg",
        "нижний новгород" to "Nizhny Novgorod",
        "казань" to "Kazan",
        "челябинск" to "Chelyabinsk",
        "омск" to "Omsk",
        "самара" to "Samara",
        "ростов-на-дону" to "Rostov-on-Don",
        "ростов на дону" to "Rostov-on-Don",
        "уфа" to "Ufa",
        "красноярск" to "Krasnoyarsk",
        "пермь" to "Perm",
        "воронеж" to "Voronezh",
        "волгоград" to "Volgograd"
    )

    /**
     * Преобразовать введённое пользователем имя города:
     * - обрезать пробелы
     * - привести к нижнему регистру для поиска по словарю
     * - при необходимости транслитерировать с русского на английский
     */
    private fun normalizeCityNameForApi(rawName: String): String {
        val trimmed = rawName.trim()
        if (trimmed.isEmpty()) return trimmed

        val lower = trimmed.lowercase()

        // 1. Сначала пробуем готовый словарь популярных городов
        russianToEnglishCityMap[lower]?.let { mapped ->
            return mapped
        }

        // 2. Если в словаре нет, но в названии есть кириллица — пробуем транслитерацию
        val hasCyrillic = lower.any { it in 'а'..'я' || it == 'ё' }
        return if (hasCyrillic) {
            transliterateRuToEn(lower)
        } else {
            // 3. Если всё уже на латинице — отправляем как есть
            trimmed
        }
    }

    /**
     * Простая транслитерация русского названия города в латиницу.
     * Не идеальна, но работает для большинства городов.
     */
    private fun transliterateRuToEn(source: String): String {
        val map = mapOf(
            'а' to "a",  'б' to "b",  'в' to "v",  'г' to "g",  'д' to "d",
            'е' to "e",  'ё' to "e",  'ж' to "zh", 'з' to "z",  'и' to "i",
            'й' to "y",  'к' to "k",  'л' to "l",  'м' to "m",  'н' to "n",
            'о' to "o",  'п' to "p",  'р' to "r",  'с' to "s",  'т' to "t",
            'у' to "u",  'ф' to "f",  'х' to "kh", 'ц' to "ts", 'ч' to "ch",
            'ш' to "sh", 'щ' to "shch", 'ъ' to "",  'ы' to "y",  'ь' to "",
            'э' to "e",  'ю' to "yu", 'я' to "ya"
        )

        val builder = StringBuilder()
        for (ch in source) {
            when {
                ch == ' ' || ch == '-' || ch == '–' -> builder.append(' ')
                ch in 'a'..'z' -> builder.append(ch)
                map.containsKey(ch) -> builder.append(map[ch])
                else -> builder.append(ch)
            }
        }

        // Убираем лишние пробелы по краям и заменяем двойные пробелы на один
        return builder.toString()
            .trim()
            .replace(Regex("\\s+"), " ")
    }
    
    override suspend fun getCityCoordinates(cityName: String): City? {
        return try {
            // Преобразуем название города для запроса в API
            val normalizedName = normalizeCityNameForApi(cityName)

            // Выполняем запрос к API
            val cities = cityApiService.getCity(normalizedName)
            
            // Проверяем, что получен хотя бы один результат
            if (cities.isEmpty()) {
                null
            } else {
                // Берем первый результат и преобразуем в domain entity
                CityMapper.toDomain(cities.first())
            }
        } catch (e: Exception) {
            null
        }
    }
}

