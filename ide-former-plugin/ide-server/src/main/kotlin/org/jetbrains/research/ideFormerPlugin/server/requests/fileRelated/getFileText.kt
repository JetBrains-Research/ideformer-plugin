package org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.FileText
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getFileText(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-text/{fileName?}") {
        // TODO: extract to the separate function
        val fileName = call.parameters["fileName"] ?: run {
            logger.error("File name was not provided")
            return@get call.respondJson(
                IdeServerConstants.MISSING_FILENAME,
                HttpStatusCode.BadRequest
            )
        }
        logger.info("Server GET file text request for file '$fileName' is called")

        val fileText = FileText(ideStateKeeper.currentProjectDirectory, fileName)
        if (!executeAndRespondError(fileText, logger)) {
            return@get
        }

        call.respondJson(fileText.getFileText()!!)
        logger.info("Server GET file text request for file '$fileName' is processed")
    }
}