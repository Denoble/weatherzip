package com.example.weatherzip.repository.network

import com.example.weatherzip.model.location.LocationAddress
import com.example.weatherzip.model.nearby.NearByLocation
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val LOCATION_BASE_URL = "https://maps.googleapis.com/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(LOCATION_BASE_URL)
    .build()

interface LocationAPIService {
    @GET("maps/api/geocode/json")
    fun getaddress(
        @Query("latlng") latlng: String,
        @Query("location_type") location_type: String = "ROOFTOP",
        @Query("result_type") result_type: String = "street_address",
        @Query("key") key: String
    ): Deferred<LocationAddress>

    @GET("/maps/api/place/nearbysearch/json")
    fun getNearbyPlaces(
        @Query("keyword") keyword: String = "restaurant",
        @Query("location") location: String,
        @Query("radius") within: Int = 5500,
        @Query("type") type: String = "restaurant",
        @Query("key") key: String
    ): Deferred<NearByLocation>
}

object LocationApi {
    val locationRetrofitServices: LocationAPIService
            by lazy { retrofit.create(LocationAPIService::class.java) }
}