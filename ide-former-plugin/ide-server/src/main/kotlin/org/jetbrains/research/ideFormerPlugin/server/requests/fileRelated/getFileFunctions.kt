package org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.utils.chooseFileFunctionsApiForFile
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.processFileNameParameter
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getFileFunctions(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-functions/{fileName?}") {
        val fileName = call.processFileNameParameter(logger)
            ?: return@get
        logger.info("Server GET file methods request for file '$fileName' is called")

        val fileFunctions = try {
            chooseFileFunctionsApiForFile(fileName, ideStateKeeper.currentProjectDirectory)
        } catch (e: Exception) {
            logger.error(e.message)
            return@get call.respondJson(
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