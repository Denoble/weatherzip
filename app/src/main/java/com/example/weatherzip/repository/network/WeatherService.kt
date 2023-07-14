package com.example.weatherzip.repository.network

import com.example.weatherzip.model.location.LocationAddress
import com.example.weatherzip.model.nearby.NearByLocation
import com.example.weatherzip.model.weather.OpenWeatherData
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val OPENWEATHER_MAP_BASE_URL = "https://api.openweathermap.org"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(OPENWEATHER_MAP_BASE_URL)
    .build()

interface OpenWeatherAPIService {
    @GET("/data/3.0/onecall")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") key: String
    ): Deferred<OpenWeatherData>
}

object weatherApi {
    val weatherRetrofitServices: OpenWeatherAPIService
            by lazy { retrofit.create(OpenWeatherAPIService::class.java) }
}