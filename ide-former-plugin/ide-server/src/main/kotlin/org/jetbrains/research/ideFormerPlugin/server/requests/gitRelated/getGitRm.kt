package org.jetbrains.research.ideFormerPlugin.server.requests.gitRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitAdd
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitRm
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getGitRm(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    // TODO: add the ability to send a list of file names in the request
    get("/git-rm/{${IdeServerConstants.FILE_NAME_REQUEST_PARAM}?}") {
        val fileName = call.processFileNameParameter(logger) ?: return@get
        logger.info("Server GET git rm for the file '$fileName' is called")

        val gitRm = GitRm(ideStateKeeper.userProject, ideStateKeeper.projectGitRoot, listOf(fileName))
        if (!executeAndRespondError(gitRm, logger)) return@get

        ideStateKeeper.saveReversibleApiMethod(gitRm)
        logger.info("Git rm api method was saved on the api methods stack")

        call.respondJson("File $fileName was successfully removed from the Git Index")
        logger.info("Server GET git rm for the file '$fileName' is processed")
    }
}