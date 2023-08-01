package ru.informen.models

import kotlinx.serialization.Serializable

@Serializable
data class MapData(
    val lat: Double,
    val lon: Double,
)