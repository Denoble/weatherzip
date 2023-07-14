package com.example.weatherzip.model.weather

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenWeatherData(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val minutely: List<Minutely>,
    val timezone: String,
    val timezone_offset: Int
)