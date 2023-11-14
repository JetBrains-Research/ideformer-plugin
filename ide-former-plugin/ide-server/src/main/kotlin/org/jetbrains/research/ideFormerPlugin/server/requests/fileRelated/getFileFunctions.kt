package org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.models.utils.chooseFileFunctionsApiForFile
import org.jetbrains.research.ideFormerPlugin.server.*
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.FILE_NAME_REQUEST_PARAM
import org.jetbrains.research.ideFormerPlugin.server.IdeServerConstants.FUNCTION_NAME_REQUEST_PARAM
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Routing.getFileFunctions(logger: Logger, ideStateKeeper: IdeStateKeeper) {
    get("/file-functions/{$FILE_NAME_REQUEST_PARAM?}{$FUNCTION_NAME_REQUEST_PARAM?}") {
        val fileName = call.processFileNameParameter(logger) ?: return@get
        logger.info("Server GET file functions request for file '$fileName' is called")

        val fileFunctions = try {
            chooseFileFunctionsApiForFile(fileName, ideStateKeeper.currentProjectDirectory)
        } catch (e: Exception) {
            logger.error(e.message)
            return@get call.respondJson(
                // TODO: mb this is redundant bc there is the thrown error has its own message
                IdeServerConstants.INCORRECT_REQUESTED_FILE_EXTENSION,
                HttpStatusCode.BadRequest
            )
        }

        if (!executeAndRespondError(fileFunctions, logger)) {
            return@get
        }

        when (val functionName = call.parameters[FUNCTION_NAME_REQUEST_PARAM]) {
            null -> {
                call.respondJson(fileFunctions.getFunctionsNames()!!)
                logger.info("Server GET file functions request for file '$fileName' is processed")
            }
            else -> {
                val functionCode = try {
                    fileFunctions.getFunctionCode(functionName)
                } catch (e: Exception) {
                    e.message!!
                }

                call.respondJson(functionCode)
                logger.info("Server GET file function code request for file '$fileName' and function '$functionName' is processed")
            }
        }
    }
}