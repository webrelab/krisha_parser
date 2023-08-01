package ru.informen.properties

import org.jsoup.nodes.Document
import ru.informen.models.ViewResponse

class BuyFlat(
    url: String, name: String, price: Long, lat: Double, lon: Double,
    rooms: Int, liveSquare: String, condition: String,
    val bathroom: String,
    val floor: String,
    val houseYear: String,
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
        fun of(response: ViewResponse, url: String, document: Document): BuyFlat {
            val contentExtractor = ContentExtractor(document)
            return BuyFlat(
                url = url,
                price = response.advert.price,
                name = response.advert.title,
                lat = response.advert.map.lat,
                lon = response.advert.map.lon,
                rooms = response.advert.rooms,
                liveSquare = contentExtractor.primary("live.square"),
                buildingType = contentExtractor.primary("flat.building"),
                houseYear = contentExtractor.primary("house.year"),
                bathroom = contentExtractor.primary("flat.toilet"),
                floor = contentExtractor.primary("flat.floor"),
                condition = contentExtractor.primary("flat.renovation"),
            )
        }
    }

    init {
        names.putAll(
            linkedMapOf(
                "Этаж" to floor,
                "Туалет" to bathroom,
                "Год постройки" to houseYear,
                "Тип строения" to buildingType
            )
        )
    }
}