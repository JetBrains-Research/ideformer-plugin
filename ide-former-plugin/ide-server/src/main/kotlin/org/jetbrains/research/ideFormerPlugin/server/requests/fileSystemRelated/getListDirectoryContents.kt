package org.jetbrains.research.ideFormerPlugin.server.requests.fileSystemRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated.ListDirectoryContents
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getListDirectoryContents(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/list-dir-contents/{dirName?}") {
        val dirName = call.parameters["dirName"]
        logger.info("Server GET ls request for dir '$dirName' is called")

        val listDirectoryContents = dirName?.let {
            ListDirectoryContents(ideStateKeeper.currentProjectDirectory, it)
        } ?: ListDirectoryContents(ideStateKeeper.currentProjectDirectory)

        if (!executeAndRespondError(listDirectoryContents, logger)) {
            return@get
        }

        call.respondJson(listDirectoryContents.getSearchDirectoryItemsNames()!!)
        logger.info("Server GET ls request for dir '$dirName' is processed")
    }
}