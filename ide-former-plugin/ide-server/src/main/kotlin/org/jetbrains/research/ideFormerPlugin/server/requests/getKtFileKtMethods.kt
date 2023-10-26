package org.jetbrains.research.ideFormerPlugin.server.requests

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.KtFileKtMethods
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants
import org.jetbrains.research.ideFormerPlugin.server.jsonConverter
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getKtFileKtMethods(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-kt-methods/{fileName}") {
        val fileName = call.parameters["fileName"] ?: return@get call.respondText(
            text = IdeServerConstants.MISSING_FILENAME,
            status = HttpStatusCode.BadRequest
        )
        logger.info("Server GET file kt methods request for file '$fileName' is called")

        val ktFileKtMethods = KtFileKtMethods(ideStateKeeper.currentProjectDirectory, fileName)
        try {
            ktFileKtMethods.execute()
        } catch (e: Exception) {
            logger.error("Error while kt file kt methods api execution: ${e.message}")
            return@get call.respondText(
                jsonConverter.toJson(e.message ?: IdeServerConstants.API_EXECUTION_UNKNOWN_ERROR)
            )
        }

        call.respondText(jsonConverter.toJson(ktFileKtMethods.getFileKtMethodsNames()))
        logger.info("Server GET file kt methods request for file '$fileName' is processed")

    }
}