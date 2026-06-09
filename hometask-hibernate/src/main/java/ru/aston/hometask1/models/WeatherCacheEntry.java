package ru.aston.hometask1.models;

import java.time.Instant;

public class WeatherCacheEntry {
    private final String forecast;
    private final Instant cachedAt;

    public WeatherCacheEntry(String forecast, Instant cachedAt) {
        this.forecast = forecast;
        this.cachedAt = cachedAt;
    }

    public String getForecast() { return forecast; }
    public Instant getCachedAt() { return cachedAt; }
}
