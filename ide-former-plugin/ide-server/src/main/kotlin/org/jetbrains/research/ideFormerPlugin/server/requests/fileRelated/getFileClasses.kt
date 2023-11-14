package org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.utils.chooseFileClassesApiForFile
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.CLASSNAME_REQUEST_PARAMETER
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.FILENAME_REQUEST_PARAMETER
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getFileClasses(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-classes/{$FILENAME_REQUEST_PARAMETER?}{$CLASSNAME_REQUEST_PARAMETER?}") {
        val fileName = call.processFileNameParameter(logger) ?: return@get
        logger.info("Server GET file classes request for the file '$fileName' is called")

        val fileClasses = try {
            chooseFileClassesApiForFile(fileName, ideStateKeeper.currentProjectDirectory)
        } catch (e: Exception) {
            logger.error(e.message)
            return@get call.respondJson(
                // TODO: mb this is redundant bc there is the thrown error has its own message
                IdeServerConstants.INCORRECT_REQUESTED_FILE_EXTENSION,
                HttpStatusCode.BadRequest
            )
        }

        if (!executeAndRespondError(fileClasses, logger)) {
            return@get
        }

        val className = call.parameters[CLASSNAME_REQUEST_PARAMETER]
        if (className == null) {
            call.respondJson(fileClasses.getClassesNames()!!)
            logger.info("Server GET file classes request for the file '$fileName' is processed")
        } else {
            val classCode = try {
                fileClasses.getClassCode(className)
            } catch (e: Exception) {
                e.message!!
            }
            call.respondJson(classCode)
            logger.info("Server GET file class code request for the file '$fileName' and the class '$className' is processed")
        }
    }
}
