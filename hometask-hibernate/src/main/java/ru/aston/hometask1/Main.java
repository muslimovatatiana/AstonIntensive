package ru.aston.hometask1;

import ru.aston.hometask1.models.WeatherCacheEntry;
import ru.aston.hometask1.models.WeatherRequest;

import java.time.Instant;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        AstonHashMap<WeatherRequest, WeatherCacheEntry> weatherCache = new AstonHashMap<>();

        System.out.println("=== Шаг 1: Первое обращение (Кэш пуст) ===");

        WeatherRequest requestMsk = new WeatherRequest("Moscow", "2026-05-17");

        WeatherCacheEntry cachedForecast = weatherCache.get(requestMsk);
        if (cachedForecast == null) {
            System.out.println("[API] Запрос к удаленному серверу погоды для Москвы...");
            String remoteData = "Ясно, +18°C";
            weatherCache.put(requestMsk, new WeatherCacheEntry(remoteData, Instant.now()));
        }

        System.out.println("\n=== Шаг 2: Повторный запрос через 1 секунду ===");

        Thread.sleep(1000);
        WeatherCacheEntry secondAttempt = weatherCache.get(requestMsk);
        if (secondAttempt != null) {
            System.out.println("[КЭШ] Данные успешно взяты из нашей AstonHashMap!");
            System.out.println("Погода: " + secondAttempt.getForecast() +
                    " (Время кэширования: " + secondAttempt.getCachedAt() + ")");
        }

        System.out.println("\n=== Шаг 3: Наполнение кэша и использование Стримов ===");

        weatherCache.put(new WeatherRequest("Ufa", "2026-05-17"), new WeatherCacheEntry("Пасмурно, +12°C", Instant.now()));
        weatherCache.put(new WeatherRequest("London", "2026-05-17"), new WeatherCacheEntry("Дождь, +10°C", Instant.now()));

        System.out.println("Текущее количество записей в кэше: " + weatherCache.size());

        System.out.print("Города в кэше: ");
        List<String> cachedCities = weatherCache.keysStream()
                .map(WeatherRequest::getCity)
                .toList();
        System.out.println(cachedCities);

        System.out.println("\n=== Шаг 4: Инвалидация (удаление) ===");

        WeatherCacheEntry removed = weatherCache.remove(requestMsk);
        System.out.println("Удалено из кэша значение: " + (removed != null ? removed.getForecast() : "нет"));
        System.out.println("Проверка после удаления (Москва): " + weatherCache.get(requestMsk));
        System.out.println("Итоговый размер кэша: " + weatherCache.size());
    }
}
