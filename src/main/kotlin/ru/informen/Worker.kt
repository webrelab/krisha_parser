package ru.informen

import ru.informen.buildings.BuildingsLoader
import ru.informen.models.Item
import ru.informen.models.RequestParams
import ru.informen.models.SchoolBuilding
import ru.informen.parser.KrishaParser
import javax.naming.LimitExceededException

class Worker(private val requestParams: RequestParams) {

    fun execute(): String {
        val properties = try {
            KrishaParser(requestParams.searchUrl).also { it.parse() }.getProperties()
        } catch (e: LimitExceededException) {
            return "limit-exceeding"
        } catch (e: IllegalArgumentException) {
            return e.message ?: "unknown-error"
        } catch (e: Throwable) {
            return "server-error/${e.stackTraceToString()}"
        }
        val schools = loadSchools()
        val metroStations = when (requestParams.metro) {
            true -> BuildingsLoader.get("places/metro_stations.xlsx")
            false -> emptyList()
        }
        val cityParks = when (requestParams.cityPark) {
            true -> BuildingsLoader.get("places/city_parks.xlsx")
            false -> emptyList()
        }

        val propertiesToSchool = findNearestBuildings(properties, schools)
        val propertiesToMetroStation = findNearestBuildings(properties, metroStations)
        val propertiesToCityParks = findNearestBuildings(properties, cityParks)
        val items = propertiesToSchool.map {
            Item(
                it.key,
                it.value,
                propertiesToMetroStation[it.key] ?: emptyList(),
                propertiesToCityParks[it.key] ?: emptyList(),
            )
        }.sortedBy {
            if (it.schoolRoute.isEmpty()) {
                if (requestParams.metro) {
                    it.metroRoute[0].distance
                } else if (requestParams.cityPark) {
                    it.cityParks[0].distance
                } else {
                    1000.0
                }
            } else {
                it.schoolRoute[0].distance
            }
        }
        val fileId = ResultWriter(items).writeResultsToXlsx()
        return "download/$fileId"
    }

    private fun loadSchools(): List<SchoolBuilding> {
        val schoolTypes = mutableListOf<String>()
        if (requestParams.generalSchool) schoolTypes.add("general")
        if (requestParams.gymnasium) schoolTypes.add("gymnasium")
        if (requestParams.lyceum) schoolTypes.add("lyceum")
        if (requestParams.gossad) schoolTypes.add("gossad")
        if (requestParams.correctionalsad) schoolTypes.add("correctionalsad")
        if (requestParams.privatesad) schoolTypes.add("privatesad")
        val languages = mutableListOf<String>()
        if (requestParams.rusLang) languages.add("rus-lang")
        if (requestParams.kazLang) languages.add("kaz-lang")
        if (requestParams.mixedLang) languages.add("mixed-lang")
        val files = schoolTypes.flatMap {
            languages.map { lang -> "${it}_$lang" }
        }.map { "schools/almaty_${it}.xlsx" }
        return files.flatMap {
            BuildingsLoader.getSchools(it)
        }
    }
}