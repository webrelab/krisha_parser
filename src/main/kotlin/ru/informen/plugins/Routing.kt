package ru.informen.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.logging.error
import mu.KotlinLogging
import ru.informen.Worker
import ru.informen.fileStorage
import ru.informen.fromCheckbox
import ru.informen.models.RequestParams

private val logger = KotlinLogging.logger { }
fun Application.configureRouting() {
    routing {
        post("/startProcessing") {
            try {
                val params = call.receiveParameters()
                params["searchUrl"]?.let {
                    val checkUrl = checkUrl(it)
                    if (checkUrl.isNotEmpty()) return@post call.respondText(checkUrl)
                    val requestParams = RequestParams(
                        searchUrl = it,
                        kazLang = params["kaz-lang"].fromCheckbox(),
                        rusLang = params["rus-lang"].fromCheckbox(),
                        mixedLang = params["mixed-lang"].fromCheckbox(),
                        generalSchool = params["general"].fromCheckbox(),
                        gymnasium = params["gymnasium"].fromCheckbox(),
                        lyceum = params["lyceum"].fromCheckbox(),
                        metro = params["metro"].fromCheckbox(),
                        cityPark = params["city-park"].fromCheckbox(),
                        gossad = params["gossad"].fromCheckbox(),
                        correctionalsad = params["correctionalsad"].fromCheckbox(),
                        privatesad = params["privatesad"].fromCheckbox()
                    )
                    val fileName = Worker(requestParams).execute()
                    call.respondText(fileName)
                } ?: call.respond(BadRequest, "Не указан URL фильтра")
            } catch (e: Throwable) {
                logger.error(e)
                val respondText = "/server-error/${e.stackTraceToString().replace("/", "|").take(1000)}"
                logger.info(respondText)
                call.respondText(respondText)
            }
        }
        get("/download/{fileId}") {
            val fileId = call.parameters["fileId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            fileStorage[fileId]?.let {
                call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"result.xlsx\"")
                call.respondFile(it.toFile())
            } ?: call.respondText("/file-not-found")

        }
        get("/file-not-found") {
            call.respondText("Что-то пошло неправильно и файл отчёта потерялся. Попробуй выполнить операцию ещё раз")
        }
        get("/limit-exceeding") {
            call.respondText {
                "К сожалению, превышен лимит выдачи по указанному фильтру. Попробуй более детальный фильтр, " +
                    "что бы уменьшить количество объектов до 200"
            }
        }
        get("/wrong-search-url") {
            call.respondText {
                "Проверь правильность ссылки с фильтрами. Должно начинаться на https://krisha.kz/"
            }
        }
        get("/unknown-error") {
            call.respondText {
                "Произошла неизвестная ошибка. Просьба сообщить об этом в https:/t.me/webrelab"
            }
        }
        get("/unknown-filter-data") {
            call.respondText {
                "Не удалось распознать поисковый запрос. Убедись, что в фильтрах выбрана продажа или аренда, " +
                    "а так же это должен быть либо дом, либо квартира."
            }
        }
        get("/server-error/{stacktrace}") {
            call.parameters["stacktrace"]?.let {
                call.respondText(it)
            } ?: call.respondText("Неизвестная ошибка сервера")
        }
        staticResources("/", "static")
    }
}

private fun checkUrl(url: String): String {
    return if (!url.startsWith("https://krisha.kz/")) "/wrong-search-url"
    else ""
}