package org.jetbrains.research.ideFormerPlugin.server

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod
import org.jetbrains.research.ideFormerPlugin.server.requests.*
import org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated.getFileMethods
import org.jetbrains.research.ideFormerPlugin.server.requests.fileSystemRelated.getChangeDirectory
import org.jetbrains.research.ideFormerPlugin.server.requests.fileSystemRelated.getListDirectoryContents
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Application.configureRouting(ideStateKeeper: IdeStateKeeper, logger: Logger) {
    install(IgnoreTrailingSlash)

    routing {
        getMainPage(logger)
        getIdeApiList(logger)
        getProjectModules(logger, ideStateKeeper)
        getFileMethods(logger, ideStateKeeper)
        getListDirectoryContents(logger, ideStateKeeper)
        getChangeDirectory(logger, ideStateKeeper)
        getReverseApiMethods(logger, ideStateKeeper)
        postFinalAnswer(logger, ideStateKeeper)
    }
}

object RoutingUtils {
    val jsonConverter = Gson()
}

suspend fun ApplicationCall.respondJson(responseObject: Any, status: HttpStatusCode = HttpStatusCode.OK) {
    this.respondText(
        text = RoutingUtils.jsonConverter.toJson(responseObject),
        status = status
    )
}

suspend fun PipelineContext<*, ApplicationCall>.executeAndRespondError(
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

object IdeServerConstants {
    const val NO_API_AVAILABLE = "No IDE API available"
    const val ROOT_PAGE_TEXT = "IDE server"
    const val MISSING_FILENAME = "Missing file name"
    const val PROJECT_DIR_REMAINS_THE_SAME = "Project directory remains the same"
    const val PROJECT_DIR_WAS_CHANGED_TO = "Project directory was successfully changed to"
    const val NEGATIVE_API_METHODS_CNT = "apiCallsCount parameter should be a positive integer"
    const val NOT_A_NUMBER_API_METHODS_CNT = "apiCallsCount parameter should be a number"
    const val API_EXECUTION_UNKNOWN_ERROR = "Unknown error while api method execution"
}