package ru.informen.properties

import org.jsoup.nodes.Document
import ru.informen.models.ViewResponse

class RentHouse(
    url: String, name: String, price: Long, lat: Double, lon: Double,
    rooms: Int, liveSquare: String, condition: String,
    val square: String,
    val levels: String,
    val bathroom: String,
    val electricity: String,
    val sewerage: String,
    val heating: String,
    val gas: String,
    val water: String,
    val buildingType: String,
) : Property(
    url = url,
    name = name,
    price = price,
    lat = lat,
    lon = lon,
    rooms = rooms,
    liveSquare = liveSquare,
    condition = condition
) {

    companion object {
        fun of(response: ViewResponse, url: String, document: Document): RentHouse {
            val contentExtractor = ContentExtractor(document)
            return RentHouse(
                url = url,
                price = response.advert.price,
                name = response.advert.title,
                lat = response.advert.map.lat,
                lon = response.advert.map.lon,
                rooms = response.advert.rooms,
                liveSquare = contentExtractor.primary("live.square"),
                buildingType = contentExtractor.primary("house.building"),
                levels = contentExtractor.primary("house.floor_num"),
                bathroom = contentExtractor.primary("house.toilet"),
                condition = contentExtractor.primary("house.renovation"),
                square = contentExtractor.secondary("land.square"),
                electricity = contentExtractor.secondary("cmtn.electricity"),
                gas = contentExtractor.secondary("cmtn.gas"),
                sewerage = contentExtractor.secondary("cmtn.sewage"),
                water = contentExtractor.secondary("cmtn.water"),
                heating = contentExtractor.secondary("cmtn.heating"),
            )
        }
    }

    init {
        names.putAll(
            linkedMapOf(
                "Площадь участка" to square,
                "Этажей" to levels,
                "Туалет" to bathroom,
                "Электричество" to electricity,
                "Канализация" to sewerage,
                "Отопление" to heating,
                "Газификация" to gas,
                "Вода" to water,
                "Тип и год строения" to buildingType
            )
        )
    }
}