package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.ProjectModules
import org.jetbrains.research.ideFormerPlugin.server.jsonConverter
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getProjectModules(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/project-modules") {
        logger.info("Server GET project modules request is called")

        val projectModules = ProjectModules(ideStateKeeper.userProject)
        projectModules.execute()

        call.respondText(jsonConverter.toJson(projectModules.getProjectModulesNames()))
        logger.info("Server GET project modules request is processed")
    }
}