package ru.aston.hometask1.models;

import java.util.Objects;

public class WeatherRequest {
    private final String city;
    private final String date;

    public WeatherRequest(String city, String date) {
        this.city = city;
        this.date = date;
    }

    public String getCity() { return city; }
    public String getDate() { return date; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherRequest nations = (WeatherRequest) o;
        return Objects.equals(city, nations.city) && Objects.equals(date, nations.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, date);
    }
}
