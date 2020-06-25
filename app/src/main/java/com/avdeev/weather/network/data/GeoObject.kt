package com.avdeev.weather.network.data

data class GeoObject(
    val metaDataProperty: GeocoderMetaDataObject,
    val name: String,
    val description: String,
    val point: Point
)