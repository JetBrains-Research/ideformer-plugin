package org.jetbrains.research.ideFormerPlugin.server.requests.gitRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitMerge
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getGitMerge(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/git-merge/{${IdeServerConstants.BRANCH_NAME_REQUEST_PARAM}?}") {
        val mergingBranchName = call.processRequestParameter(IdeServerConstants.BRANCH_NAME_REQUEST_PARAM, logger)
            ?: return@get
        logger.info("Server GET git merge $mergingBranchName into current branch is called")

        val gitMerge = GitMerge(ideStateKeeper.userProject, ideStateKeeper.projectGitRoot, mergingBranchName)
        if (!executeAndRespondError(gitMerge, logger)) return@get

        ideStateKeeper.saveReversibleApiMethod(gitMerge)
        logger.info("Git merge api method was saved on the api methods stack")

        call.respondJson("Git merge $mergingBranchName into current branch was processed successfully")
        logger.info("Server GET git merge $mergingBranchName into current branch is processed")
    }
}