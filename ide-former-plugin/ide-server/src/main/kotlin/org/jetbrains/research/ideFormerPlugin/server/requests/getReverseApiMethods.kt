package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.jsonConverter
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getReverseApiMethods(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/reverse-api-methods/{apiMethodsCount?}") {
        val apiMethodsCountString = call.parameters["apiMethodsCount"] ?: IdeServerConstants.DEFAULT_API_CALLS_CNT

        val apiMethodsCount = apiMethodsCountString.toIntOrNull() ?: return@get call.respondText(
            text = IdeServerConstants.NOT_A_NUMBER_API_CALLS_CNT,
            status = HttpStatusCode.BadRequest
        )
        if (apiMethodsCount <= 0) return@get call.respondText(
            text = IdeServerConstants.NEGATIVE_API_CALLS_CNT,
            status = HttpStatusCode.BadRequest
        )

        logger.info("Server GET reverse $apiMethodsCount api methods request is called")

        val reversedApiCallsCount = ideStateKeeper.reverseLastApiMethods(apiMethodsCount)
        call.respondText(jsonConverter.toJson("Last $reversedApiCallsCount api calls were reversed"))
        logger.info("Server GET reverse $apiMethodsCount api methods request is processed")
    }
}