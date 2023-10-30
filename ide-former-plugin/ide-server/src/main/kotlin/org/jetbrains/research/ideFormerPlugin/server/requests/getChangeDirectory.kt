package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.ChangeDirectory
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.API_EXECUTION_UNKNOWN_ERROR
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.PROJECT_DIR_REMAINS_THE_SAME
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.PROJECT_DIR_WAS_CHANGED_TO
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getChangeDirectory(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/change-dir/{targetDirName?}") {
        val targetDirName = call.parameters["targetDirName"]
        logger.info("Server GET cd result request for dir '$targetDirName' is called")

        val changeDirectory = targetDirName?.let {
            ChangeDirectory(ideStateKeeper, it)
        } ?: ChangeDirectory(ideStateKeeper)

        try {
            changeDirectory.execute()
        } catch (e: Exception) {
            logger.error("Error while change directory api execution: ${e.message}")
            return@get call.respondJson(e.message ?: API_EXECUTION_UNKNOWN_ERROR)
        }

        ideStateKeeper.saveReversibleApiMethod(changeDirectory)
        logger.info("Change directory api method was saved on the api calls stack")

        val response = targetDirName?.let {
            "$PROJECT_DIR_WAS_CHANGED_TO $targetDirName."
        } ?: PROJECT_DIR_REMAINS_THE_SAME

        call.respondJson(response)
        logger.info("Server GET cd result request for dir '$targetDirName' is processed")
    }
}