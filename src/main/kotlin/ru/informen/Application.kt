package ru.informen

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import ru.informen.plugins.configureRouting

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}

