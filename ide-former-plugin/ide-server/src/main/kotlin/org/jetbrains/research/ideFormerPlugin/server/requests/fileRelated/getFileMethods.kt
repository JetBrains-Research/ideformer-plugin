package org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated

import com.intellij.util.PathUtil.getFileExtension
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions.JavaFileFunctions
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions.KtFileFunctions
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions.PyFileFunctions
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getFileMethods(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-methods/{fileName}") {
        val fileName = call.parameters["fileName"] ?: return@get call.respondJson(
            IdeServerConstants.MISSING_FILENAME,
            HttpStatusCode.BadRequest
        )
        logger.info("Server GET file methods request for file '$fileName' is called")

        val fileExtension = getFileExtension(fileName)
        val fileFunctions = when (fileExtension) {
            "kt" -> KtFileFunctions(ideStateKeeper.currentProjectDirectory, fileName)
            "java"  -> JavaFileFunctions(ideStateKeeper.currentProjectDirectory, fileName)
            "py" -> PyFileFunctions(ideStateKeeper.currentProjectDirectory, fileName)
            else -> null
        }

        if (fileFunctions == null) {
            logger.error("Incorrect request file extension: $fileExtension")
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