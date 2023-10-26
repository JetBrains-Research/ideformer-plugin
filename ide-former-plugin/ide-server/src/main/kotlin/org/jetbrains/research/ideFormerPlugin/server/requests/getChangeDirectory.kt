package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.ChangeDirectory
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.API_EXECUTION_UNKNOWN_ERROR
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getChangeDirectory(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/change-dir/{targetDirName?}") {
        val targetDirName = call.parameters["targetDirName"]
        logger.info("Server GET cd result request for dir '$targetDirName' is called")

        val changeDirectory = if (targetDirName == null) {
            ChangeDirectory(ideStateKeeper)
        } else {
            ChangeDirectory(ideStateKeeper, targetDirName)
        }

        try {
            changeDirectory.execute()
        } catch (e: Exception) {
            logger.error("Error while change directory api execution: ${e.message}")
            return@get call.respondJson(e.message ?: API_EXECUTION_UNKNOWN_ERROR)
        }

        ideStateKeeper.saveReversibleApiMethod(changeDirectory)
        logger.info("Change directory api method was saved on the api calls stack")

        val response = if (targetDirName == null) {
            "Project directory remains the same."
        } else {
            "Project directory was successfully changed to $targetDirName."
        }

        call.respondJson(response)
        logger.info("Server GET cd result request for dir '$targetDirName' is processed")
    }
}