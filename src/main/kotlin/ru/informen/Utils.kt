package ru.informen

import org.apache.poi.ss.usermodel.WorkbookFactory
import ru.informen.models.Place
import ru.informen.models.Route
import ru.informen.properties.Property
import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.math.cos
import kotlin.math.sqrt

val fileStorage = mutableMapOf<String, Path>()

fun findNearestBuildings(properties: List<Property>, buildings: List<Place>): Map<Property, List<Route>> {
    val nearestBuildings = mutableMapOf<Property, List<Route>>()
    for (property in properties) {
        val routes = buildings.map { building ->
            val distance = euclideanDistance(property.lat, property.lon, building.lat, building.lon)
            Route(building, distance)
        }
            .sortedBy { it.distance }
            .take(3)

        nearestBuildings[property] = routes
    }
    return nearestBuildings
}

fun readBuildingsFromXlsx(filename: String): List<Place> {
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(filename)
        ?: throw FileNotFoundException("Файл не найден: $filename")
    val workbook = WorkbookFactory.create(inputStream)
    val sheet = workbook.getSheetAt(0)
    val buildings = mutableListOf<Place>()

    for (row in sheet) {
        if (row.physicalNumberOfCells > 2) {
            val name = row.getCell(0).stringCellValue
            val address = row.getCell(1).stringCellValue
            val location = row.getCell(2).stringCellValue.split(",").map { it.trim() }
            buildings.add(Place(name, address, location[0].toDouble(), location[1].toDouble()))
        }
    }
    workbook.close()
    return buildings
}

private fun euclideanDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val x = lat2 - lat1
    val y = (lon2 - lon1) * cos(Math.toRadians((lat1 + lat2) / 2))
    // высчитываем расстояние по прямой, а так как прямо никто не ходит, увеличиваем расстояние на 30%
    return sqrt(x * x + y * y) * 111.0 * 1.3
}

fun String?.fromCheckbox() = this == "on"