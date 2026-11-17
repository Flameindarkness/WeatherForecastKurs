# Прогноз погоды (курсовая работа)

Android-приложение на Kotlin и Jetpack Compose. Показывает прогноз погоды для списка городов, который задаёт пользователь.

- Координаты города определяются по названию через [Api-Ninjas City API](https://api-ninjas.com/api/city).
- Почасовой прогноз температуры на 16 дней загружается из [Open-Meteo](https://open-meteo.com/).
- Для каждого города есть вкладки «Сегодня», «24 часа», «Завтра» и «7 дней».

## Запуск

1. Получите бесплатный ключ на https://api-ninjas.com/.
2. Добавьте его в `local.properties` в корне проекта:
   ```
   API_NINJAS_KEY=ваш_ключ
   ```
3. Откройте проект в Android Studio и запустите конфигурацию `app`.

## Архитектура

По рекомендациям Google (https://developer.android.com/topic/architecture):

- `data`: Retrofit-сервисы, DTO, мапперы и реализации репозиториев;
- `domain`: сущности, интерфейсы репозиториев и use case'ы;
- `presentation`: `WeatherViewModel` (MVVM, `StateFlow`) и экраны на Compose;
- `di`: модули Hilt.
