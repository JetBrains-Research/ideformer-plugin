package org.jetbrains.research.ideFormerPlugin.server

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod
import org.slf4j.Logger

object RoutingUtils {
    val jsonConverter = Gson()
}

suspend fun ApplicationCall.respondJson(responseObject: Any, status: HttpStatusCode = HttpStatusCode.OK) {
    this.respondText(
        text = RoutingUtils.jsonConverter.toJson(responseObject),
        status = status
    )
}

suspend fun ApplicationCall.processRequestParameter(
    parameterName: String,
    errorMessage: String,
    logger: Logger
): String? =
    this.parameters[parameterName] ?: run {
        logger.error(errorMessage)
        this.respondJson(
            errorMessage,
            HttpStatusCode.BadRequest
        )
        null
    }

const val FILENAME_REQUEST_PARAMETER = "fileName"

suspend fun ApplicationCall.processFileNameParameter(logger: Logger): String? =
    this.processRequestParameter(
        FILENAME_REQUEST_PARAMETER,
        IdeServerConstants.MISSING_FILENAME,
        logger
    )

suspend fun PipelineContext<Unit, ApplicationCall>.executeAndRespondError(
    ideApiMethod: IdeApiMethod,
    logger: Logger
): Boolean = try {
    ideApiMethod.execute()
    true
} catch (e: Exception) {
    logger.error("Error while api execution: ${e.message}")
    call.respondJson(
        e.message ?: IdeServerConstants.API_EXECUTION_UNKNOWN_ERROR
    )
    false
}