package org.jetbrains.research.ideFormerPlugin.server.requests.gitRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitLog
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getGitLog(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/git-log") {
        logger.info("Server GET git log is called")

        val gitLog = GitLog(ideStateKeeper.userProject, ideStateKeeper.projectGitRoot)
        if (!executeAndRespondError(gitLog, logger)) return@get

        call.respondJson(gitLog.gitCommandOutputString!!)
        logger.info("Server GET git status is processed")
    }
}