package com.avdeev.weather.network.data

data class GeocoderResponseMetaData(
    val point: Point,
    val request: String,
    val results: Int,
    val found: Int
)