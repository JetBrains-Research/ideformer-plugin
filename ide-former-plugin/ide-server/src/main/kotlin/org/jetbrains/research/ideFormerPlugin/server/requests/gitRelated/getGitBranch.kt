package org.jetbrains.research.ideFormerPlugin.server.requests.gitRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitBranch
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.BRANCH_NAME_REQUEST_PARAM
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getGitBranch(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/git-branch/{$BRANCH_NAME_REQUEST_PARAM?}") {
        val branchName = call.processRequestParameter(BRANCH_NAME_REQUEST_PARAM, logger)
            ?: return@get
        logger.info("Server GET git branch $branchName is called")

        val gitBranch = GitBranch(ideStateKeeper.userProject, ideStateKeeper.projectGitRoot, branchName)
        if (!executeAndRespondError(gitBranch, logger)) return@get

        call.respondJson("Git branch $branchName was processed successfully")
        logger.info("Server GET git branch $branchName is processed")
    }
}