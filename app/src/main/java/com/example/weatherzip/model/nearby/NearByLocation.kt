package com.example.weatherzip.model.nearby

data class NearByLocation(
    val html_attributions: List<Any>,
    val next_page_token: String,
    val results: List<Result>,
    val status: String
)