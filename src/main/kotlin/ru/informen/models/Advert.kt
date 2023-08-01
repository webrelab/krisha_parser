package ru.informen.models

import kotlinx.serialization.Serializable

@Serializable
data class Advert(
    val id: Long,
    val price: Long,
    val title: String,
    val rooms: Int,
    val map: MapData,
)