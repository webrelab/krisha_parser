package ru.informen.properties

import org.jsoup.nodes.Document

class ContentExtractor(
    private val document: Document
) {

    fun primary(dataName: String) = document
        .selectXpath(getXpath(dataName))
        .first()
        ?.text()
        ?: "-"

    fun secondary(dataName: String) = document
        .selectXpath("//dt[@data-name='$dataName']/following-sibling::dd")
        .first()
        ?.text()
        ?: "-"

    private fun getXpath(dataName: String) =
        "//div[@data-name = '$dataName']/div[@class = 'offer__advert-short-info']"
}