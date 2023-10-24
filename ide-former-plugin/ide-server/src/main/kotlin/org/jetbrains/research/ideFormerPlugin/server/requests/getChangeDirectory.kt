package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.DEFAULT_DIRECTORY_NAME
import org.jetbrains.research.ideFormerPlugin.api.models.ChangeDirectory
import org.jetbrains.research.ideFormerPlugin.server.jsonConverter
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getChangeDirectory(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/change-dir/{targetDirName?}") {
        // TODO: make this default parameter a separate constant (or just call the method without a parameter -- the default one will be put inside the method class)
        val targetDirName = call.parameters["targetDirName"] ?: "."
        logger.info("Server GET cd result request for dir '$targetDirName' is called")

        val changeDirectory = ChangeDirectory(ideStateKeeper, targetDirName)
        changeDirectory.execute()

        // TODO: if there were exception in method execution, do not save this method and add the error description
        ideStateKeeper.saveReversibleApiMethod(changeDirectory)
        logger.info("Change directory api method was saved on the api calls stack")

        val response = if (targetDirName == DEFAULT_DIRECTORY_NAME) {
            "Project directory remains the same."
        } else {
            "Project directory was successfully changed to $targetDirName."
        }

        call.respondText(jsonConverter.toJson(response))
        logger.info("Server GET cd result request for dir '$targetDirName' is processed")
    }
}