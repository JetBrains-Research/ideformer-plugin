package org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.utils.chooseFileClassesApiForFile
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.processFileNameParameter
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getFileClasses(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-classes/{fileName?}") {
        val fileName = call.processFileNameParameter(logger)
            ?: return@get
        logger.info("Server GET file classes request for file '$fileName' is called")

        val fileClasses = try {
            chooseFileClassesApiForFile(fileName, ideStateKeeper.currentProjectDirectory)
        } catch (e: Exception) {
            logger.error(e.message)
            return@get call.respondJson(
                IdeServerConstants.INCORRECT_REQUESTED_FILE_EXTENSION,
                HttpStatusCode.BadRequest
            )
        }

        if (!executeAndRespondError(fileClasses, logger)) {
            return@get
        }

        call.respondJson(fileClasses.getClassesNames()!!)
        logger.info("Server GET file classes request for file '$fileName' is processed")
    }
}