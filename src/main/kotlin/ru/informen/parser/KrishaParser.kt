package ru.informen.parser

import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.informen.models.SearchResponse
import ru.informen.models.ViewResponse
import ru.informen.properties.BuyFlat
import ru.informen.properties.BuyHouse
import ru.informen.properties.Property
import ru.informen.properties.PropertyCache
import ru.informen.properties.RentFlat
import ru.informen.properties.RentHouse
import ru.informen.properties.UnknownProperty
import javax.naming.LimitExceededException

private const val JSDATA = "jsdata"
private const val PROPERTY_VIEW_URL = "https://krisha.kz/a/show"
private val logger = KotlinLogging.logger {  }

class KrishaParser(private val searchUrl: String) {

    private val ids = mutableListOf<Long>()
    private val properties = mutableListOf<Property>()
    private val json = Json { ignoreUnknownKeys = true }
    private val searchType = SearchType.determine(searchUrl)

    fun parse(page: Int? = null) {
        if (searchType == SearchType.UNDEFINED) throw IllegalArgumentException("unknown-filter-data")
        val url = getPageUrl(page)
        val jsdata = getJsdata(Jsoup.connect(url).get())
        val response: SearchResponse = json.decodeFromString(jsdata)
        if (response.search.nbTotal.replace("\\D".toRegex(), "").toInt() > 200) {
            throw LimitExceededException("")
        }
        if (response.search.ids.isEmpty()) {
            parseIds()
        } else {
            ids.addAll(response.search.ids)
            val nexPage = page?.let { it + 1 } ?: 2
            parse(nexPage)
        }
    }

    fun getProperties() = properties.toList()

    private fun getPageUrl(page: Int?) = page?.let { "$searchUrl&page=$page" } ?: searchUrl

    private fun parseIds() = ids.forEach { parseId(it) }

    private fun parseId(id: Long) {
        if (!PropertyCache.has(id)) {
            logger.info { "Property id $id not cached" }
            val url = "$PROPERTY_VIEW_URL/$id"
            val document = Jsoup.connect(url).get()
            val jsdata = getJsdata(document)
            val response: ViewResponse = json.decodeFromString(jsdata)
            val property = createProperty(response, url, document)
            PropertyCache.add(id, property)
            Thread.sleep(500)
        }
        properties.add(PropertyCache.get(id))
    }

    private fun createProperty(response: ViewResponse, url: String, document: Document): Property {
        return when (searchType) {
            SearchType.BUY_HOUSE -> BuyHouse.of(response, url, document)
            SearchType.BUY_FLAT -> BuyFlat.of(response, url, document)
            SearchType.RENT_FLAT -> RentFlat.of(response, url, document)
            SearchType.RENT_HOUSE -> RentHouse.of(response, url, document)
            else -> UnknownProperty()
        }
    }

    private fun getJsdata(document: Document) = document
        .select("#$JSDATA")
        .first()
        ?.data()
        ?.let {
            val firstIndex = it.indexOf('{')
            val lastIndex = it.lastIndexOf('}')
            it.substring(firstIndex, lastIndex + 1)
        }
        ?: throw RuntimeException("Не загрузились данные")
}

