package com.avdeev.weather.network.data

data class GeoObjectCollection (
    val metaDataProperty: MetaDataProperty,
    val featureMember: Array<GeoObjectData>
)