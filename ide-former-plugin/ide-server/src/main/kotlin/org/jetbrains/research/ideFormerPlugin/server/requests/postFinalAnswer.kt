package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.SaveModelFinalAns
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.postFinalAnswer(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    post("/post-final-ans") {
        logger.info("Server POST final model answer request is called")
        val modelFinalAns = call.receiveText()

        val saveModelFinalAns = SaveModelFinalAns(modelFinalAns)
        if (!executeAndRespondError(saveModelFinalAns, logger)) {
            return@post
        }
        logger.info("Save model final ans api call was executed")

        ideStateKeeper.saveReversibleApiMethod(saveModelFinalAns)
        logger.info("Save model final ans api call was added to the api methods stack")

        call.respondJson("Final answer was successfully saved")
        logger.info("Server POST final model answer request is processed")
    }
}