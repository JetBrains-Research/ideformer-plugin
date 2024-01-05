package org.jetbrains.research.ideFormerPlugin.server.requests.fileSystemRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated.CreateDirectory
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.DIRECTORY_NAME_REQUEST_PARAM
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getCreateDirectory(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/create-directory/{$DIRECTORY_NAME_REQUEST_PARAM?}") {
        val directoryName = call.processRequestParameter(
            DIRECTORY_NAME_REQUEST_PARAM,
            logger
        ) ?: return@get
        logger.info("Server GET create '$directoryName' directory request is called")

        val createDirectory = CreateDirectory(ideStateKeeper.currentProjectDirectory, directoryName)
        if (!executeAndRespondError(createDirectory, logger)) return@get

        ideStateKeeper.saveReversibleApiMethod(createDirectory)
        logger.info("Create directory api method was saved on the api methods stack")

        call.respondJson("A directory with the name '$directoryName' has been created successfully")
        logger.info("Server GET create '$directoryName' directory request is processed")
    }
}