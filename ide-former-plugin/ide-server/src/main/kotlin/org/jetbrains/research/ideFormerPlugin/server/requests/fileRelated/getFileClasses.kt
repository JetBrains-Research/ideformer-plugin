package org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated

import com.intellij.util.PathUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses.JavaFileClasses
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses.KtFileClasses
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses.PyFileClasses
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getFileClasses(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-classes/{fileName?}") {
        // TODO: extract to the separate function
        val fileName = call.parameters["fileName"] ?: run {
            logger.error("File name was not provided")
            return@get call.respondJson(
                IdeServerConstants.MISSING_FILENAME,
                HttpStatusCode.BadRequest
            )
        }
        logger.info("Server GET file classes request for file '$fileName' is called")

        val fileExtension = PathUtil.getFileExtension(fileName)
        val fileClasses = when (fileExtension) {
            "kt" -> KtFileClasses(ideStateKeeper.currentProjectDirectory, fileName)
            "java" -> JavaFileClasses(ideStateKeeper.currentProjectDirectory, fileName)
            "py" -> PyFileClasses(ideStateKeeper.currentProjectDirectory, fileName)
            else -> null
        }

        // TODO: extract to the separate function
        if (fileClasses == null) {
            logger.error("Incorrect request file extension: $fileExtension")
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