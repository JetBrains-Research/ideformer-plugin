package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.ProjectModules
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.API_EXECUTION_UNKNOWN_ERROR
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.API_RES_GETTING_BEFORE_EXECUTION_ERROR
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getProjectModules(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/project-modules") {
        logger.info("Server GET project modules request is called")

        val projectModules = ProjectModules(ideStateKeeper.userProject)
        try {
            projectModules.execute()
        } catch (e: Exception) {
            logger.error("Error while project modules api execution: ${e.message}")
            return@get call.respondJson(e.message ?: API_EXECUTION_UNKNOWN_ERROR)
        }

        call.respondJson(
            projectModules.getProjectModulesNames()
                ?: API_RES_GETTING_BEFORE_EXECUTION_ERROR
        )
        logger.info("Server GET project modules request is processed")
    }
}