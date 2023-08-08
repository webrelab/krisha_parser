package ru.informen.models

data class RequestParams(
    val searchUrl: String,
    val kazLang: Boolean,
    val rusLang: Boolean,
    val mixedLang: Boolean,
    val generalSchool: Boolean,
    val gymnasium: Boolean,
    val lyceum: Boolean,
    val gossad: Boolean,
    val correctionalsad: Boolean,
    val privatesad: Boolean,
    val metro: Boolean,
    val cityPark: Boolean,
)
