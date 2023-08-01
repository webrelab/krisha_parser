package ru.informen.properties

import org.jsoup.nodes.Document
import ru.informen.models.ViewResponse

class RentFlat(
    url: String, name: String, price: Long, lat: Double, lon: Double,
    rooms: Int, liveSquare: String, condition: String,
    val bathroom: String,
    val floor: String,
    val furniture: String,
    val hasFurniture: String,
    val facilities: String,
    val whoMatch: String,
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
        fun of(response: ViewResponse, url: String, document: Document): RentFlat {
            val contentExtractor = ContentExtractor(document)
            return RentFlat(
                url = url,
                price = response.advert.price,
                name = response.advert.title,
                lat = response.advert.map.lat,
                lon = response.advert.map.lon,
                rooms = response.advert.rooms,
                liveSquare = contentExtractor.primary("live.square"),
                bathroom = contentExtractor.secondary("separated_toilet"),
                floor = contentExtractor.primary("flat.floor"),
                condition = contentExtractor.primary("flat.rent_renovation"),
                furniture = contentExtractor.primary("live.furniture"),
                hasFurniture = contentExtractor.secondary("flat.furniture"),
                facilities = contentExtractor.secondary("flat.facilities"),
                whoMatch = contentExtractor.secondary("flat.facilities")
            )
        }
    }

    init {
        names.putAll(
            linkedMapOf(
                "Этаж" to floor,
                "Туалет" to bathroom,
                "Мебилирована" to furniture,
                "Мебель" to hasFurniture,
                "Удобства" to facilities,
                "Кому подойдёт" to whoMatch
            )
        )
    }
}