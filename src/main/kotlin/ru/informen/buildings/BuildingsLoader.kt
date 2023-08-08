package ru.informen.buildings

import ru.informen.models.Place
import ru.informen.models.SchoolBuilding
import ru.informen.readBuildingsFromXlsx

object BuildingsLoader {

    private val buildings = mutableMapOf<String, List<Place>>()

    fun get(fileName: String): List<Place> {
        if (!buildings.contains(fileName)) {
            buildings[fileName] = readBuildingsFromXlsx(fileName)
        }
        return buildings.getValue(fileName)
    }

    fun getSchools(fileName: String): List<SchoolBuilding> {

        if (!buildings.contains(fileName)) {
            val lang = when {
                fileName.contains("mixed-lang") -> "Смешанный"
                fileName.contains("rus-lang") -> "Русский"
                else -> "Казахский"
            }
            buildings[fileName] = readBuildingsFromXlsx(fileName)
                .map {
                    SchoolBuilding(
                        name = it.name,
                        address = it.address,
                        lat = it.lat,
                        lon = it.lon,
                        lang = lang
                    )
                }
        }
        return buildings.getValue(fileName) as List<SchoolBuilding>
    }
}