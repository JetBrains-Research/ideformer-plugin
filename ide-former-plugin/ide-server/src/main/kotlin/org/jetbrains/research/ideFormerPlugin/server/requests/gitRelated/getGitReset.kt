package org.jetbrains.research.ideFormerPlugin.server.requests.gitRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitReset
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getGitReset(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/git-reset/{${IdeServerConstants.COMMITS_COUNT_REQUEST_PARAM}?}") {
        // TODO: fix parsing
        val commitsCount = call.parameters[IdeServerConstants.COMMITS_COUNT_REQUEST_PARAM]?.toIntOrNull() ?: return@get
        logger.info("Server GET git reset for '$commitsCount' commits is called")

        val gitReset = GitReset(ideStateKeeper.userProject, ideStateKeeper.projectGitRoot, commitsCount)
        if (!executeAndRespondError(gitReset, logger)) return@get

        ideStateKeeper.saveReversibleApiMethod(gitReset)
        logger.info("Git reset api method was saved on the api methods stack")

        call.respondJson("$commitsCount commits were successfully reset")
        logger.info("Server GET git reset for '$commitsCount' commits is processed")
    }
}