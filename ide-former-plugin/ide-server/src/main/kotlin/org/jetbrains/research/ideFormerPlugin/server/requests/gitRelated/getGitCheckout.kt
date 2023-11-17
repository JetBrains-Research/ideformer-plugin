package org.jetbrains.research.ideFormerPlugin.server.requests.gitRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitCheckout
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.BRANCH_NAME_REQUEST_PARAM
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getGitCheckout(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/git-checkout/{$BRANCH_NAME_REQUEST_PARAM?}") {
        val branchName = call.processRequestParameter(BRANCH_NAME_REQUEST_PARAM, logger)
            ?: return@get
        logger.info("Server GET git checkout $branchName is called")

        val gitCheckout = GitCheckout(ideStateKeeper.userProject, ideStateKeeper.projectGitRoot, branchName)
        if (!executeAndRespondError(gitCheckout, logger)) return@get

        call.respondJson("Git checkout $branchName was processed successfully")
        logger.info("Server GET git checkout $branchName is processed")
    }
}