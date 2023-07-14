package com.example.weatherzip.model.location

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationAddress(
    @field:Json(name = "plus_code")
    val plusCode: PlusCode = PlusCode(),
    val results: List<Result> = listOf(),
    val status: String = ""
)
@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "address_components")
    val addressComponents: List<AddressComponent> = listOf(),
    @Json(name = "formatted_address")
    val formattedAddress: String = "",
    val geometry: Geometry = Geometry(),
    @Json(name = "place_id")
    val placeId: String = "",
    @Json(name = "plus_code")
    val plusCode: PlusCodeX = PlusCodeX(),
    val types: List<String> = listOf()
)
@JsonClass(generateAdapter = true)
data class AddressComponent(
    @Json(name = "long_name")
    val longName: String = "",
    @Json(name = "short_name")
    val shortName: String = "",
    val types: List<String> = listOf()
)
@JsonClass(generateAdapter = true)
data class Geometry(
    val location: Location = Location(),
    @Json(name = "location_type")
    val locationType: String = "",
    val viewport: Viewport = Viewport()
)
@JsonClass(generateAdapter = true)
data class Location(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)
@JsonClass(generateAdapter = true)
data class Northeast(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)
@JsonClass(generateAdapter = true)
data class PlusCode(
    @Json(name = "compound_code")
    val compoundCode: String = "",
    @Json(name = "global_code")
    val globalCode: String = ""
)
@JsonClass(generateAdapter = true)
data class PlusCodeX(
    @Json(name = "compound_code")
    val compoundCode: String = "",
    @Json(name = "global_code")
    val globalCode: String = ""
)
@JsonClass(generateAdapter = true)
data class Southwest(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)
@JsonClass(generateAdapter = true)
data class Viewport(
    val northeast: Northeast = Northeast(),
    val southwest: Southwest = Southwest()
)