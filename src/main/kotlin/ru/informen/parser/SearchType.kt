package ru.informen.parser

enum class SearchType(val urlPart: String) {
    BUY_HOUSE("prodazha/doma"),
    BUY_FLAT("prodazha/kvartiry"),
    RENT_HOUSE("arenda/doma"),
    RENT_FLAT("arenda/kvartiry"),
    UNDEFINED(""),
    ;

    companion object {
        fun determine(url: String) = values().first { url.contains(it.urlPart) }
    }
}