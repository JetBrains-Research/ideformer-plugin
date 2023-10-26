package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.jsonConverter
import org.slf4j.Logger

fun Routing.getMainPage(logger: Logger) {
    get("/") {
        logger.info("Server GET root page request is called")
        call.respondText(
            jsonConverter.toJson(IdeServerConstants.ROOT_PAGE_TEXT)
        )
        logger.info("Server GET root page request is processed")
    }
}