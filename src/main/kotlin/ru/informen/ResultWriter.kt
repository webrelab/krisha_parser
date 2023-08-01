package ru.informen

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.informen.models.Item
import ru.informen.models.SchoolBuilding
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.UUID

class ResultWriter(private val results: List<Item>) {

    private val workbook = XSSFWorkbook()
    private val sheet = workbook.createSheet("Results")
    private val hasSchools = results[0].schoolRoute.isNotEmpty()
    private val hasMetro = results[0].metroRoute.isNotEmpty()
    private val hasCityParks = results[0].cityParks.isNotEmpty()

    fun writeResultsToXlsx(): String {
        writeHeader()
        writeRows()
        var columns = results[0].property.names.size
        if (hasMetro) columns++
        if (hasCityParks) columns++
        if (hasSchools) {
            columns += 3
            sheet.defaultRowHeight = (sheet.defaultRowHeight * 3).toShort()
        }
        repeat(columns) { sheet.autoSizeColumn(it) }
        val report = Files.createTempFile("report-", "xlsx")
        FileOutputStream(report.toFile()).use { outputStream ->
            workbook.write(outputStream)
        }
        return UUID.randomUUID().toString().also { fileStorage[it] = report }
    }

    private fun writeHeader() {
        val headerRow = sheet.createRow(0)
        val keys = results[0].property.names.keys
        var index = 0
        for (name in keys) headerRow.createCell(index++).setCellValue(name)
        if (hasMetro) headerRow.createCell(index++).setCellValue("Метро")
        if (hasCityParks) headerRow.createCell(index++).setCellValue("Парки")
        if (hasSchools) (1..3).forEach { headerRow.createCell(index++).setCellValue("Школа $it") }
    }

    private fun writeRows() {
        results.forEachIndexed { index, item ->
            sheet.createRow(index + 1).also { writeRow(item, it) }
        }
    }

    private fun writeRow(item: Item, row: XSSFRow) {
        val values = item.property.names.values
        var index = 0
        for (value in values) row.createCell(index++).setCellValue(value.toString())
        if (hasMetro) item.metroRoute[0]
            .let { "${it.place.name} - ${formatDistance(it.distance)}" }
            .also { row.createCell(index++).setCellValue(it) }

        if (hasCityParks) item.cityParks[0]
            .let { "${it.place.name} - ${formatDistance(it.distance)}" }
            .also { row.createCell(index++).setCellValue(it) }

        if (hasSchools) item.schoolRoute.forEach { route ->
            "${route.place.name} - ${(route.place as SchoolBuilding).lang}\n${route.place.address}\n${formatDistance(route.distance)}"
                .also { row.createCell(index++).setCellValue(it) }
        }
    }

    private fun formatDistance(distanceInKm: Double): String {
        return if (distanceInKm < 1) {
            "${(distanceInKm * 1000).toInt()} м"
        } else {
            val distanceInKmRounded = if (distanceInKm % 1 == 0.0) {
                distanceInKm.toInt()
            } else {
                String.format("%.1f", distanceInKm)
            }
            "$distanceInKmRounded км"
        }
    }
}