package ru.informen.models

import kotlinx.serialization.Serializable

@Serializable
data class SearchResults(
    val currentPage: Long,
    val ids: List<Long>,
    val nbTotal: String,
)