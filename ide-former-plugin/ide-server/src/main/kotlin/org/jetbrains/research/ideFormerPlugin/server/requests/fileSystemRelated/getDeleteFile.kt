package org.jetbrains.research.ideFormerPlugin.server.requests.fileSystemRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated.DeleteFile
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getDeleteFile(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/delete-file/{fileName?}") {
        val fileName = call.processFileNameParameter(logger) ?: return@get
        logger.info("Server GET delete '$fileName' file request is called")

        val deleteFile = DeleteFile(ideStateKeeper.currentProjectDirectory, fileName)
        if (!executeAndRespondError(deleteFile, logger)) return@get

        ideStateKeeper.saveReversibleApiMethod(deleteFile)
        logger.info("Delete file api method was saved on the api methods stack")

        call.respondJson("A file with the name '$fileName' has been deleted successfully")
        logger.info("Server GET delete '$fileName' file request is processed")
    }
}