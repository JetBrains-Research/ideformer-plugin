package org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.utils.chooseFileFunctionsApiForFile
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getFileFunctions(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-functions/{$FILENAME_REQUEST_PARAMETER?}") {
        val fileName = call.processFileNameParameter(logger) ?: return@get
        logger.info("Server GET file methods request for file '$fileName' is called")

        val fileFunctions = try {
            chooseFileFunctionsApiForFile(fileName, ideStateKeeper.currentProjectDirectory)
        } catch (e: Exception) {
            logger.error(e.message)
            return@get call.respondJson(
                // TODO: mb this is redundant bc there is the thrown error has its own message
                IdeServerConstants.INCORRECT_REQUESTED_FILE_EXTENSION,
                HttpStatusCode.BadRequest
            )
        }

        if (!executeAndRespondError(fileFunctions, logger)) {
            return@get
        }

        call.respondJson(fileFunctions.getFunctionsNames()!!)
        logger.info("Server GET file methods request for file '$fileName' is processed")
    }
}