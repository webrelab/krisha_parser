package ru.informen.models

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val search: SearchResults
)