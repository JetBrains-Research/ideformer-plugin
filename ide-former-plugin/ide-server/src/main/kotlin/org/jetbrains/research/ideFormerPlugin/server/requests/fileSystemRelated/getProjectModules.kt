package org.jetbrains.research.ideFormerPlugin.server.requests.fileSystemRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated.ProjectModules
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getProjectModules(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/project-modules") {
        logger.info("Server GET project modules request is called")

        val projectModules = ProjectModules(ideStateKeeper.userProject)
        if (!executeAndRespondError(projectModules, logger)) {
            return@get
        }

        call.respondJson(projectModules.getProjectModulesNames()!!)
        logger.info("Server GET project modules request is processed")
    }
}