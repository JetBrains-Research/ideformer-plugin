package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.SaveModelFinalAns
import org.jetbrains.research.ideFormerPlugin.server.jsonConverter
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.postFinalAnswer(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    post("/post-final-ans") {
        logger.info("Server POST final model answer request is called")
        val modelFinalAns = call.receiveText()

        val apiMethod = SaveModelFinalAns(modelFinalAns)
        apiMethod.execute()
        logger.info("Save model final ans api call was executed")

        ideStateKeeper.saveReversibleApiMethod(apiMethod)
        logger.info("Save model final ans api call was added to the api methods stack")

        call.respondText(jsonConverter.toJson("Model ans was successfully saved"))
        logger.info("Server POST final model answer request is processed")
    }
}