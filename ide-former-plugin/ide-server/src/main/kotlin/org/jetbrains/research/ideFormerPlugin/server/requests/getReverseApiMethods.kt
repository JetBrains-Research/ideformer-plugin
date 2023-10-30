package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getReverseApiMethods(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/reverse-api-methods/{apiMethodsCount?}") {
        val apiMethodsCountString = call.parameters["apiMethodsCount"]

        val reversedApiCallsCount = if (apiMethodsCountString != null) {
            val apiMethodsCount = apiMethodsCountString.toIntOrNull() ?: run {
                logger.error("Not a number api methods count parameter for reverse api method request")
                call.respondJson(
                    IdeServerConstants.NOT_A_NUMBER_API_METHODS_CNT,
                    HttpStatusCode.BadRequest
                )
                return@get
            }

            if (apiMethodsCount <= 0) {
                logger.error("Negative api methods count parameter for reverse api method")
                call.respondJson(
                    IdeServerConstants.NEGATIVE_API_METHODS_CNT,
                    HttpStatusCode.BadRequest
                )
                return@get
            }

            logger.info("Server GET reverse $apiMethodsCount api methods request is called")
            ideStateKeeper.reverseLastApiMethods(apiMethodsCount)
        } else {
            logger.info("Server GET reverse api methods request with default method count is called")
            ideStateKeeper.reverseLastApiMethods()
        }

        call.respondJson("Last $reversedApiCallsCount api calls were reversed")
        logger.info("Server GET reverse api methods request is processed")
    }
}