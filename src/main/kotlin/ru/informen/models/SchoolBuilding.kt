package ru.informen.models

class SchoolBuilding(
    name: String,
    address: String,
    lat: Double,
    lon: Double,
    val lang: String,
) : Place(
    name = name,
    address = address,
    lat = lat,
    lon = lon,
)
