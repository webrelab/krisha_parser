package ru.informen.properties

abstract class Property(
    val url: String,
    val name: String,
    val price: Long,
    val lat: Double,
    val lon: Double,
    val rooms: Int,
    val liveSquare: String,
    val condition: String,
) {

    open val names: LinkedHashMap<String, Any> = linkedMapOf(
        "Заголовок" to name,
        "Ссылка" to url,
        "Цена" to price,
        "Комнат" to rooms,
        "Общая площадь" to liveSquare,
        "Состояние" to condition,
    )
}