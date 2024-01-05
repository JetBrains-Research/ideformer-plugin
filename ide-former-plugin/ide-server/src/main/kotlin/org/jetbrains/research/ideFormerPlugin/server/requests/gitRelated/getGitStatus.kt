package org.jetbrains.research.ideFormerPlugin.server.requests.gitRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitStatus
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getGitStatus(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/git-status") {
        logger.info("Server GET git status is called")

        val gitStatus = GitStatus(ideStateKeeper.userProject, ideStateKeeper.projectGitRoot)
        if (!executeAndRespondError(gitStatus, logger)) return@get

        call.respondJson(gitStatus.gitCommandOutputString!!)
        logger.info("Server GET git status is processed")
    }
}