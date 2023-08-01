package ru.informen.models

import ru.informen.properties.Property

data class Item(
    val property: Property,
    val schoolRoute: List<Route>,
    val metroRoute: List<Route>,
    val cityParks: List<Route>,
)
