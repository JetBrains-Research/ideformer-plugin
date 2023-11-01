package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions.KtFileFunctions
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.executeAndRespondError
import org.jetbrains.research.ideFormerPlugin.server.respondJson
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getKtFileKtMethods(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-kt-methods/{fileName}") {
        val fileName = call.parameters["fileName"] ?: return@get call.respondJson(
            IdeServerConstants.MISSING_FILENAME,
            HttpStatusCode.BadRequest
        )
        logger.info("Server GET file kt methods request for file '$fileName' is called")

        val ktFileFunctions = KtFileFunctions(ideStateKeeper.currentProjectDirectory, fileName)
        if (!executeAndRespondError(ktFileFunctions, logger)) {
            return@get
        }

        call.respondJson(ktFileFunctions.getFileFunctionsNames()!!)
        logger.info("Server GET file kt methods request for file '$fileName' is processed")
    }
}