package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServer
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.jsonConverter
import org.slf4j.Logger

fun Routing.getIdeApiList(logger: Logger) {
    get("/ide-api-list") {
        logger.info("Server GET ide api list request is called")

        val apiDescriptions =
            IdeServer::class.java.getResource("ideApiDescriptions.json")!!.readText()
        apiDescriptions.ifEmpty { IdeServerConstants.NO_API_AVAILABLE }
        logger.info("Api descriptions were retrieved")

        call.respondText(jsonConverter.toJson(apiDescriptions))
        logger.info("Server GET ide api list request is processed")
    }
}