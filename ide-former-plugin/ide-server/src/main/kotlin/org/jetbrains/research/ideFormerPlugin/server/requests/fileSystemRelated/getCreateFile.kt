package org.jetbrains.research.ideFormerPlugin.server.requests.fileSystemRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated.CreateFile
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getCreateFile(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/create-file/{fileName?}") {
        val fileName = call.processFileNameParameter(logger) ?: return@get
        logger.info("Server GET create '$fileName' file request is called")

        val createFile = CreateFile(ideStateKeeper.currentProjectDirectory, fileName)
        if (!executeAndRespondError(createFile, logger)) return@get

        ideStateKeeper.saveReversibleApiMethod(createFile)
        logger.info("Create file api method was saved on the api methods stack")

        call.respondJson("A file with name '$fileName' has been created successfully")
        logger.info("Server GET create '$fileName' file request is processed")
    }
}