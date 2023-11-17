package org.jetbrains.research.ideFormerPlugin.server.requests.gitRelated

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.gitRelated.GitAdd
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.FILE_NAME_REQUEST_PARAM
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getGitAdd(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    // TODO: add the ability to send a list of file names in the request
    get("/git-add/{$FILE_NAME_REQUEST_PARAM?}") {
        val fileName = call.processFileNameParameter(logger) ?: return@get
        logger.info("Server GET git add for the file '$fileName' is called")

        val gitAdd = GitAdd(ideStateKeeper.userProject, ideStateKeeper.projectGitRoot, listOf(fileName))
        if (!executeAndRespondError(gitAdd, logger)) return@get

        call.respondJson("File $fileName was successfully added to Git")
        logger.info("Server GET git add for the file '$fileName' is processed")
    }
}