package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.ListDirectoryContents
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.jsonConverter
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getListDirectoryContents(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/list-dir-contents/{dirName?}") {
        val dirName = call.parameters["dirName"]
        logger.info("Server GET ls request for dir '$dirName' is called")

        val listDirectoryContents = if (dirName == null) {
            ListDirectoryContents(ideStateKeeper.currentProjectDirectory)
        } else {
            ListDirectoryContents(ideStateKeeper.currentProjectDirectory, dirName)
        }

        try {
            listDirectoryContents.execute()
        } catch (e: Exception) {
            logger.info("Error while kt file kt methods api execution: ${e.message}")
            return@get call.respondText(
                jsonConverter.toJson(e.message ?: IdeServerConstants.API_EXECUTION_UNKNOWN_ERROR)
            )
        }

        call.respondText(jsonConverter.toJson(listDirectoryContents.getSearchDirectoryItemsNames()))
        logger.info("Server GET ls request for dir '$dirName' is processed")
    }
}