package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.ListDirectoryContents
import org.jetbrains.research.ideFormerPlugin.server.jsonConverter
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getListDirectoryContents(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/list-dir-contents/{dirName?}") {
        // TODO: make this default parameter a separate constant (or just call the method without a parameter -- the default one will be put inside the method class)
        val dirName = call.parameters["dirName"] ?: "."
        logger.info("Server GET ls request for dir '$dirName' is called")

        val listDirectoryContents = ListDirectoryContents(ideStateKeeper.currentProjectDirectory, dirName)
        listDirectoryContents.execute()
        // TODO: if error was caught here, return string with the error explanation


        call.respondText(jsonConverter.toJson(listDirectoryContents.getSearchDirectoryItemsNames()))
        logger.info("Server GET ls request for dir '$dirName' is processed")
    }
}