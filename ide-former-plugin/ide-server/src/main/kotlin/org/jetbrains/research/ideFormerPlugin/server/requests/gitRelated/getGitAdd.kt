package org.jetbrains.research.ideFormerPlugin.server.requests.gitRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitAdd
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getGitAdd(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/git-add") {
        // TODO: add file names list as a parameter
        logger.info("Server GET git add is called")

        val gitStatus = GitAdd(ideStateKeeper.userProject, ideStateKeeper.projectGitRoot, listOf("models/cat.py"))
        if (!executeAndRespondError(gitStatus, logger)) return@get

        call.respondJson("success")
        logger.info("Server GET git add is processed")
    }
}