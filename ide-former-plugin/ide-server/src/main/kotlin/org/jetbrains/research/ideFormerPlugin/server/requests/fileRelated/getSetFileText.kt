package org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileText.SetFileText
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.FILE_NAME_REQUEST_PARAM
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.FILE_TEXT_REQUEST_PARAM
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

// TODO: make it a post method
fun Routing.getSetFileText(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/set-file-text/{${FILE_NAME_REQUEST_PARAM}?}{${FILE_TEXT_REQUEST_PARAM}?}") {
        val fileName = call.processFileNameParameter(logger) ?: return@get
        val newText = call.processRequestParameter(FILE_TEXT_REQUEST_PARAM, logger) ?: return@get
        logger.info("Server GET set file text request for file '$fileName' is called")

        val setFileText = SetFileText(ideStateKeeper.currentProjectDirectory, fileName, newText)
        if (!executeAndRespondError(setFileText, logger)) return@get

        call.respondJson("New text was set to a file")
        logger.info("Server GET set file text request for file '$fileName' is processed")
    }
}