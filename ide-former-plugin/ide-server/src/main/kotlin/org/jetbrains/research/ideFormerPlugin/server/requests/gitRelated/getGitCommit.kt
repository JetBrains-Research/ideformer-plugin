package org.jetbrains.research.ideFormerPlugin.server.requests.gitRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitCommit
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.COMMIT_MESSAGE_REQUEST_PARAM
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getGitCommit(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/git-commit/{${COMMIT_MESSAGE_REQUEST_PARAM}?}") {
        val commitMessage = call.processRequestParameter(COMMIT_MESSAGE_REQUEST_PARAM, logger)
            ?: return@get
        logger.info("Server GET git commit with the message '$commitMessage' is called")

        val gitCommit = GitCommit(ideStateKeeper.userProject, ideStateKeeper.projectGitRoot, commitMessage)
        if (!executeAndRespondError(gitCommit, logger)) return@get

        call.respondJson("Git commit was processed successfully")
        logger.info("Server GET git commit with the message '$commitMessage' is processed")
    }
}