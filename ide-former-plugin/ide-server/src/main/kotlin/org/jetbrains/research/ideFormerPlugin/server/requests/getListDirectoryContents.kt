package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.ListDirectoryContents
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
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

        try {
            listDirectoryContents.execute()
        } catch (e: Exception) {
            logger.error("Error while kt file kt methods api execution: ${e.message}")
            return@get call.respondJson(
                e.message ?: IdeServerConstants.API_EXECUTION_UNKNOWN_ERROR
            )
        }

        call.respondJson(listDirectoryContents.getSearchDirectoryItemsNames()!!)
        logger.info("Server GET ls request for dir '$dirName' is processed")
    }
}