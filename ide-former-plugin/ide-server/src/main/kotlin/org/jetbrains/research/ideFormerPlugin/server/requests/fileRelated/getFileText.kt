package org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileText.GetFileText
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getFileText(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-text/{fileName?}") {
        val fileName = call.processFileNameParameter(logger) ?: return@get
        logger.info("Server GET file text request for file '$fileName' is called")

        val getFileTextMethod = GetFileText(ideStateKeeper.currentProjectDirectory, fileName)
        if (!executeAndRespondError(getFileTextMethod, logger)) return@get

        call.respondJson(getFileTextMethod.getFileText())
        logger.info("Server GET file text request for file '$fileName' is processed")
    }
}