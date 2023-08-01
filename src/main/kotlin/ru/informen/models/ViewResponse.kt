package ru.informen.models

import kotlinx.serialization.Serializable

@Serializable
data class ViewResponse(
    val advert: Advert
)